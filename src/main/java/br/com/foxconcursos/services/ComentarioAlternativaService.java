package br.com.foxconcursos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.foxconcursos.domain.ComentarioAlternativa;
import br.com.foxconcursos.dto.AlternativaGptDTO;
import br.com.foxconcursos.dto.ComentarioResposta;
import br.com.foxconcursos.dto.ComentarioRespostaAPI;
import br.com.foxconcursos.dto.QuestaoParaGptDTO;
import br.com.foxconcursos.repositories.ComentarioAlternativaRepository;

@Service
public class ComentarioAlternativaService {
    
    private ComentarioAlternativaRepository comentarioAlternativaRepository;

    @Value("${api.gpt.url}")
    private String gptApiUrl;

    @Value("${api.gpt.key}")
    private String apiKey;

    public ComentarioAlternativaService(ComentarioAlternativaRepository comentarioAlternativaRepository) {
        this.comentarioAlternativaRepository = comentarioAlternativaRepository;
    }

    public void gerarComentario(QuestaoParaGptDTO questaoParaGptDTO) throws Exception {
        String prompt = montarPrompt(questaoParaGptDTO);
        String modelo = obterModelo(prompt);
    
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
            "role", "user",
            "content", prompt
        ));
    
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelo);
        requestBody.put("messages", messages);
    
        String responseJson = callApi(requestBody);
    
        // 1º parse: extrair content do JSON retornado pela OpenAI
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseJson);
    
        // Verificar se a resposta tem a estrutura correta
        JsonNode choicesNode = root.path("choices");
        if (choicesNode.isArray() && choicesNode.size() > 0) {
            String conteudo = choicesNode.get(0).path("message").path("content").asText();
    
            // 2º parse: transformar a string JSON dentro do content em uma lista de ComentarioResposta
            // No método gerarComentario:
            try {
                // 1. Desserializar para a classe intermediária
                List<ComentarioRespostaAPI> respostasAPI = objectMapper.readValue(conteudo, 
                    new TypeReference<List<ComentarioRespostaAPI>>() {});
                
                // 2. Converter para o formato necessário
                List<ComentarioResposta.Comentario> comentarios = new ArrayList<>();
                
                for (ComentarioRespostaAPI resp : respostasAPI) {
                    comentarios.add(new ComentarioResposta.Comentario(
                        resp.alternativaId(),
                        resp.alternativa(),
                        resp.comentario(),
                        resp.correta()
                    ));
                }
                
                // 3. Criar o objeto final
                ComentarioResposta respostaFinal = new ComentarioResposta(
                    questaoParaGptDTO.idQuestao().toString(),
                    modelo,
                    comentarios
                );
                
                // 4. Salvar 
                salvarComentario(respostaFinal);
            } catch (JsonProcessingException e) {
                // Tratamento de erro com mais detalhes
                System.err.println("Erro ao processar JSON: " + e.getMessage());
                System.err.println("Conteúdo recebido: " + conteudo);
                throw new Exception("Erro ao processar o JSON do conteúdo gerado.", e);
            }
        } else {
            System.err.println("A resposta não contém a estrutura esperada.");
            throw new Exception("Resposta da API não contém a estrutura esperada.");
        }
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
        StringBuilder prompt = new StringBuilder();
    
        // Adicionar contexto da matéria se disponível
        prompt.append("Analise a seguinte questão de múltipla escolha ");
        // if (questao.materia() != null && !questao.materia().isEmpty()) {
        //     prompt.append("sobre " + questao.materia());
        // }
        prompt.append(".\n\n");
    
        // Enunciado
        String enunciadoLimitado = questao.enunciado().length() > 500 ? 
            questao.enunciado().substring(0, 500) + "..." : questao.enunciado();
        prompt.append("Enunciado:\n").append(enunciadoLimitado).append("\n\n");
    
        // Alternativas
        int maxAlternativas = 5;
        prompt.append("Alternativas:\n");
        int alternativasCount = 0;
        for (AlternativaGptDTO alt : questao.alternativas()) {
            if (alternativasCount >= maxAlternativas) break;
            prompt.append(alt.letra()).append(") ").append(alt.descricao()).append("\n");
            alternativasCount++;
        }
    
        // Instruções para comentários de qualidade
        prompt.append("\nPara cada alternativa, forneça um comentário detalhado e técnico que:\n");
        prompt.append("- Se a alternativa for correta: explique por que ela é a resposta certa\n");
        prompt.append("- Se a alternativa for incorreta: explique precisamente por que está errada\n");
        prompt.append("- Use linguagem técnica e precisa, mas compreensível\n");
        prompt.append("- Tenha entre 2-4 frases explicativas\n\n");
    
        // Detalhamento do formato da resposta
        prompt.append("A resposta deve estar em formato JSON assim:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"alternativa\": \"<LETRA da alternativa>\",\n");
        prompt.append("    \"comentario\": \"<justificativa técnica e detalhada>\",\n");
        prompt.append("    \"correta\": true|false,\n");
        prompt.append("    \"questaoId\": \"<ID da questão>\",\n");
        prompt.append("    \"alternativaId\": \"<ID da alternativa>\"\n");
        prompt.append("  },\n  ...\n]\n");
    
        // IMPORTANTE: Instrução sobre alternativa correta
        prompt.append("\nIMPORTANTE: Uma e apenas uma alternativa deve ser marcada como correta (correta: true). ");
        prompt.append("Analise cuidadosamente a questão e determine qual é a resposta correta baseada no conteúdo.\n");
    
        // Exemplo de comentário
        prompt.append("\nExemplo de comentário de qualidade:\n");
        prompt.append("Para alternativa correta: \"Esta alternativa está correta porque [explicação técnica]. ");
        prompt.append("O conceito se aplica adequadamente neste contexto, pois [explicação].\"\n\n");
        prompt.append("Para alternativa incorreta: \"Esta alternativa está incorreta porque [explicação do erro]. ");
        prompt.append("O correto seria [explicação da verdade].\"\n\n");
    
        // IDs das alternativas
        prompt.append("IDs das alternativas:\n");
        for (AlternativaGptDTO alt : questao.alternativas()) {
            prompt.append(alt.letra()).append(" → ").append(alt.id()).append("\n");
        }
    
        // Instrução final
        prompt.append("\nUse apenas a LETRA das alternativas no campo \"alternativa\".\n");
        prompt.append("Assegure-se de marcar exatamente uma alternativa como correta.\n");
    
        return prompt.toString();
    }
        
    private String callApi(Map<String, Object> requestBody) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // substitui "Authorization" manual

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(gptApiUrl, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new Exception("Erro ao chamar a API do ChatGPT", e);
        }
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
