package br.com.foxconcursos.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.ItemQuestaoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class PdfService {
    
    public byte[] exportarSimuladoParaPDF(String titulo, UsuarioLogado usuario,
            List<DisciplinaQuestoesResponse> disciplinas) throws Exception {

        String instrucoesHtml = new String(Files.readAllBytes(
            Path.of(getClass().getClassLoader().getResource(
                "templates/simulado/instrucoes.html").toURI())));
        
        instrucoesHtml = instrucoesHtml.replace("${titulo}", titulo);

        String rascunhoHtml = new String(Files.readAllBytes(
            Path.of(getClass().getClassLoader().getResource(
                "templates/simulado/rascunho.html").toURI())));
                
        String rodapeHtml = new String(Files.readAllBytes(
            Path.of(getClass().getClassLoader().getResource(
                "templates/simulado/rodape.html").toURI())));
        
        rodapeHtml = rodapeHtml.replace(
            "${nome}", usuario.getNome()).replace(
                "${cpf}", FoxUtils.formatarCpf(usuario.getCpf()));
        
        String contentHtml = this.getContentHtml(disciplinas);

        Document document = Jsoup.parse(contentHtml, 
            "UTF-8", Parser.xmlParser());
        Document instrucoes = Jsoup.parse(instrucoesHtml, 
            "UTF-8", Parser.xmlParser());
        Document rascunho = Jsoup.parse(rascunhoHtml, 
            "UTF-8", Parser.xmlParser());

        addFooter(document, rodapeHtml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
           
            PdfRendererBuilder builder = new PdfRendererBuilder();
            
            String combinedHtml = getStart() + instrucoes.html() + rascunho.html() + document.html() + "</body></html>";
            
            builder.withHtmlContent(combinedHtml, new File(".").toURI().toString());
            builder.toStream(os);
            builder.run();
            
            return os.toByteArray();
        }
    }

    private void addFooter(Document document, String rodapeHtml) {
        // Element body = document.body();
        // Element rodape = Jsoup.parseBodyFragment(rodapeHtml).body().child(0);
        // body.appendChild(rodape);
    }

  // Método para gerar o cabeçalho HTML
    private String getStart() {
        return """
            <html>
            <head>
                <style>
                    *{
                        text-align: justify;
                    }
                    body {
                        font-family: Arial, sans-serif;
                        font-size: 12px;
                        margin: 0;
                        padding: 0;
                    }
                    div.teste {
                        margin-top: 10px;
                        margin-bottom: 10px;
                    }
                        .gabarito-container {
                        display: flex;
                        flex-wrap: wrap;
                    }
                    .questao {
                        width: 10%;
                        text-align: center;
                        border: 1px solid #000;
                        margin-bottom: 5px;
                    }
                    .questao-numero {
                        font-weight: bold;
                    }
                    .questao-letra {
                        font-size: 14px;
                    }
                    .clear {
                        flex-basis: 100%;
                        height: 0;
                    }
                </style>
            </head>
            <body>
        """;
    }    

    // Método para gerar o conteúdo HTML das disciplinas
    private String getContentHtml(List<DisciplinaQuestoesResponse> disciplinas) {
        
        StringBuilder htmlContent = new StringBuilder();

        for (DisciplinaQuestoesResponse disciplina : disciplinas) {
            if (disciplina.getQuestoes().isEmpty()) continue;

            htmlContent.append("<h3 style='background-color: #dadada; padding: 10px 5px;font-size: 16px'>")
                    .append(disciplina.getNome())
                    .append("</h3>");

            for (QuestaoSimuladoResponse questao : disciplina.getQuestoes()) {
                htmlContent.append("<div style='margin-bottom: 0.5cm'>")
                        .append("<h5 style='font-size: 13px; margin: 0'>")
                        .append(questao.getOrdem()).append(") ")
                        .append(FoxUtils.removerTagsP(questao.getEnunciado()))
                        .append("</h5>");

                for (ItemQuestaoResponse alternativa : questao.getAlternativas()) {
                    htmlContent.append("<p style='margin: 5px 0; padding: 0'>")
                            .append(FoxUtils.obterLetra(alternativa.getOrdem())).append(") ")
                            .append(FoxUtils.removerTagsP(alternativa.getDescricao()))
                            .append("</p>");
                }

                htmlContent.append("</div>");
            }
        }

        return htmlContent.toString();
    }
}
