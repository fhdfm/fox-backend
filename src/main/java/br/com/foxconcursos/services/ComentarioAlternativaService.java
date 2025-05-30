package br.com.foxconcursos.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.foxconcursos.domain.ComentarioAlternativa;
import br.com.foxconcursos.dto.AlternativaGptDTO;
import br.com.foxconcursos.dto.ComentarioResposta;
import br.com.foxconcursos.dto.QuestaoParaGptDTO;
import br.com.foxconcursos.repositories.ComentarioAlternativaRepository;

@Service
public class ComentarioAlternativaService {
    
    private ComentarioAlternativaRepository comentarioAlternativaRepository;

    @Value("${api.gpt.url}")
    private String gptApiUrl;

    @Value("${api.gpt.key}")
    private String apiKey;

      private final HttpClient client = HttpClient.newHttpClient();
      private final Gson gson = new Gson();

    public ComentarioAlternativaService(ComentarioAlternativaRepository comentarioAlternativaRepository) {
        this.comentarioAlternativaRepository = comentarioAlternativaRepository;
    }

    public void comentar(QuestaoParaGptDTO questao) throws Exception {
        String prompt = montarPrompt(questao);
        String modelo = obterModelo(prompt);

        // Cria uma thread para a questão
        String threadId = criarThread(prompt);

        // Iniciar o run com o assistant
        String runId = iniciarRun(threadId);

        // 3. Esperar até o run ser concluído
        aguardarConclusao(threadId, runId);

        // 4. Buscar resposta final
        String respostaJson = buscarResposta(threadId);

        System.out.println("Resposta JSON: " + respostaJson);

                // 5. Interpretar JSON
        RespostaGPT resposta = gson.fromJson(respostaJson, RespostaGPT.class);
        
        List<ComentarioResposta.Comentario> comentarios = new ArrayList<>();
        comentarios.add(new ComentarioResposta.Comentario(
            resposta.getId_opcao_correta(),
            null, // Alternativa não é necessária aqui
            resposta.getExplicacao(),
            true // Considerando que a opção correta é sempre verdadeira
        ));
        
        ComentarioResposta comentarioResposta = new ComentarioResposta(
            questao.idQuestao().toString(),
            modelo,
            comentarios
        );

        salvarComentario(comentarioResposta);
    }

    private String buscarResposta(String threadId) throws Exception {
        HttpRequest request = get("https://api.openai.com/v1/threads/" + threadId + "/messages");
        String response = send(request);

        System.out.println("Resposta da API: " + response);

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        if (data == null || data.size() == 0) {
            throw new IllegalStateException("Nenhuma resposta encontrada no thread.");
        }

        JsonObject primeiraMensagem = data.get(0).getAsJsonObject();
        JsonArray contentArray = primeiraMensagem.getAsJsonArray("content");

        if (contentArray == null || contentArray.size() == 0) {
            throw new IllegalStateException("Mensagem não contém conteúdo.");
        }

        JsonObject primeiroConteudo = contentArray.get(0).getAsJsonObject();

        if (!primeiroConteudo.has("text")) {
            throw new IllegalStateException("Conteúdo inesperado no formato da mensagem.");
        }

        JsonObject textObject = primeiroConteudo.getAsJsonObject("text");

        if (!textObject.has("value")) {
            throw new IllegalStateException("Campo 'value' não encontrado na resposta.");
        }

        try {
            String valor = textObject.get("value").getAsString();
            System.out.println("Valor extraído: " + valor);
            return valor;
        } catch (Exception e) {
            System.err.println("Erro ao extrair 'value': " + textObject);
            throw e;
        }
    }


    private void aguardarConclusao(String threadId, String runId) throws Exception {
        String status;
        do {
            HttpRequest request = get("https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId);
            String response = send(request);
            status = JsonParser.parseString(response)
                               .getAsJsonObject()
                               .get("status").getAsString();
            Thread.sleep(1000);
        } while (!status.equals("completed"));
    }    

    private HttpRequest get(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")  // ← ESSENCIAL
                .build();
    }

    private  String iniciarRun(String threadId) throws Exception {
        String body = String.format("""
            {
              "assistant_id": "%s",
              "tool_choice": "auto"
            }
            """, "asst_wEqOcJwnQXbVRlvgFSOildrJ");

        HttpRequest request = request("https://api.openai.com/v1/threads/" + threadId + "/runs", body);
        String response = send(request);
        return JsonParser.parseString(response)
                         .getAsJsonObject()
                         .get("id").getAsString();
    }

    private String criarThread(String mensagem) throws Exception {
        String body = String.format("""
            {
              "messages": [
                {
                  "role": "user",
                  "content": "%s",
                  "file_ids": ["vs_68388f73b6808191974501ff1c865c1a"] 
                }   
              ]
            }
            """, escapeJson(mensagem));

        HttpRequest request = request("https://api.openai.com/v1/threads", body);
        String response = send(request);

        return JsonParser.parseString(response)
                         .getAsJsonObject()
                         .get("id").getAsString();
    }

    private String send(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private HttpRequest request(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("OpenAI-Beta", "assistants=v2")  // ← ESSENCIAL
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
    }

    private void salvarComentario(ComentarioResposta comentarioResposta) {
        // Verificação de segurança para evitar NPE
        if (comentarioResposta.comentarios() == null) {
            System.err.println("Lista de comentários nula para questão: " + comentarioResposta.questaoId());
            return;
        }
        
        for (ComentarioResposta.Comentario comentario : comentarioResposta.comentarios()) {
            ComentarioAlternativa comentarioAlternativa = new ComentarioAlternativa();
            comentarioAlternativa.setAlternativaId(UUID.fromString(comentario.alternativaId()));
            comentarioAlternativa.setComentario(comentario.comentario());
            comentarioAlternativa.setCorreta(comentario.correta());
            comentarioAlternativa.setQuestaoId(UUID.fromString(comentarioResposta.questaoId()));
            comentarioAlternativa.setModelo(comentarioResposta.modeloUtilizado());
            
            comentarioAlternativaRepository.save(comentarioAlternativa);
        }
    }
    
    private String montarPrompt(QuestaoParaGptDTO questao) {

        String opcoes = montarOpcoes(questao.alternativas());

        String prompt = """
                id_questao: %s
                banca: %s
                questao: %s

                opcoes:
                %s
            """;

        prompt = String.format(prompt, questao.idQuestao(), questao.banca(), questao.enunciado(), opcoes);

        return escapeJson(prompt);
    }

    private String montarOpcoes(List<AlternativaGptDTO> alternativas) {
        String opcoes = "";
        for (AlternativaGptDTO alternativa : alternativas) {
            opcoes += String.format("%s: %s\n", 
                alternativa.id(), alternativa.descricao());
        }
        return opcoes;
    }

    // private String callApi(Map<String, Object> requestBody) throws Exception {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.setBearerAuth(apiKey); // substitui "Authorization" manual

    //     HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

    //     RestTemplate restTemplate = new RestTemplate();

    //     try {
    //         ResponseEntity<String> response = restTemplate.postForEntity(gptApiUrl, request, String.class);
    //         return response.getBody();
    //     } catch (RestClientException e) {
    //         throw new Exception("Erro ao chamar a API do ChatGPT", e);
    //     }
    // }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    private String obterModelo(String prompt) {
        // Verifica se há imagens no prompt (tag <img> ou src="http...)
        if (prompt.contains("<img") || prompt.contains("src=\"http")) {
            return "gpt-4"; // Modelo com suporte a imagens
        }
    
        return "gpt-3.5-turbo"; // Modelo mais barato para texto puro
    }
    
    public List<ComentarioResposta.Comentario> listarComentariosPorQuestaoId(UUID questaoId) {
        
        List<ComentarioAlternativa> comentarios =
                comentarioAlternativaRepository.findByQuestaoId(questaoId);
        
        List<ComentarioResposta.Comentario> comentariosResposta = new ArrayList<>();
        
        for (ComentarioAlternativa comentario : comentarios) {
            comentariosResposta.add(new ComentarioResposta.Comentario(
                comentario.getAlternativaId().toString(),
                null,
                comentario.getComentario(),
                comentario.getCorreta()
            ));
        }

        return comentariosResposta;
    }
    
}

class RespostaGPT {
    private String id_questao;
    private String id_opcao_correta;
    private String explicacao;

    public String getId_questao() {
        return id_questao;
    }

    public void setId_questao(String id_questao) {
        this.id_questao = id_questao;
    }

    public String getId_opcao_correta() {
        return id_opcao_correta;
    }

    public void setId_opcao_correta(String id_opcao_correta) {
        this.id_opcao_correta = id_opcao_correta;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    @Override
    public String toString() {
        return "RespostaQuestao{" +
                "id_questao='" + id_questao + '\'' +
                ", id_opcao_correta='" + id_opcao_correta + '\'' +
                ", explicacao='" + explicacao + '\'' +
                '}';
    }
}

