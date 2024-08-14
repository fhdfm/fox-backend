package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.*;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    public byte[] gerarPdfFromBancoDeQuestoes(BancoQuestaoResponse bancoQuestao) throws Exception {

        String start = """
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
                            
                            #titulo-centralizado {
                                width: 100%;
                                display: flex;
                                justify-content: center;
                                background-color: #dadada;
                                padding: 10px 5px;
                                text-align: center;
                                font-size: 14px;
                                font-weight: 600;
                            }
                                
                            .table-bordered {
                                width: 100%;
                                border-collapse: collapse;
                                margin-top: 20px;
                            }
                            .table-bordered th, .table-bordered td {
                                border: 1px solid #000;
                                padding: 8px;
                                text-align: center;
                             }                    
                        </style>
                    </head>
                    <body>                
                """;

        String cabecalhoHtml = new String(Files.readAllBytes(
                Path.of(getClass().getClassLoader().getResource(
                        "templates/simulado/cabecalho.html").toURI())));

        cabecalhoHtml = cabecalhoHtml.replace("${titulo}",
                formatToString(bancoQuestao.getFiltros()));
        cabecalhoHtml = cabecalhoHtml.replace("${modelo}",
                "Banco de Questões");

        StringBuilder htmlContent = new StringBuilder();

        List<QuestaoResponse> questoes = bancoQuestao.getQuestoes();

        int i = 1;

        for (QuestaoResponse questao : questoes) {
            htmlContent.append("<div style='margin-bottom: 0.5cm'>")
                    .append("<h5 style='font-size: 13px; margin: 0'>")
                    .append(i).append(") ")
                    .append(FoxUtils.removeInvalidXMLCharacters(FoxUtils.removerTagsP(questao.getEnunciado())))
                    .append("</h5>");

            String correta = "";

            for (AlternativaResponse alternativa : questao.getAlternativas()) {
                htmlContent.append("<p style='margin: 5px 0; padding: 0'>")
                        .append(FoxUtils.obterLetra(Integer.parseInt(alternativa.getLetra()))).append(") ")
                        .append(FoxUtils.removeInvalidXMLCharacters(FoxUtils.removerTagsP(alternativa.getDescricao())))
                        .append("</p>");
                if (alternativa.getCorreta()) {
                    correta = FoxUtils.obterLetra(Integer.parseInt(alternativa.getLetra())) + "";
                }
            }
            htmlContent.append("</hr>");

            htmlContent
                    .append("<small>Alternativa correta: ")
                    .append(correta)
                    .append("</small>");

            htmlContent.append("</div>");
            i++;
        }

        Document header = Jsoup.parse(
                cabecalhoHtml, "UTF-8", Parser.xmlParser());
        Document content = Jsoup.parse(
                htmlContent.toString(), "UTF-8", Parser.xmlParser());

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            String combinedHtml = start + header.html() + content.html() + "</body></html>";

            builder.withHtmlContent(combinedHtml, new File(".").toURI().toString());
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }

    private String formatToString(Map<String, String> map) {

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey()).append(entry.getValue()).append(", ");
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2); // remove the last ", "
        }

        return sb.toString();
    }

    public byte[] exportarGabaritoParaPDF(GabaritoResponse gabarito) throws Exception {

        String start = """
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
                            
                            #titulo-centralizado {
                                width: 100%;
                                display: flex;
                                justify-content: center;
                                background-color: #dadada;
                                padding: 10px 5px;
                                text-align: center;
                                font-size: 14px;
                                font-weight: 600;
                            }
                                
                            .table-bordered {
                                width: 100%;
                                border-collapse: collapse;
                                margin-top: 20px;
                            }
                            .table-bordered th, .table-bordered td {
                                border: 1px solid #000;
                                padding: 8px;
                                text-align: center;
                }                    
                        </style>
                    </head>
                    <body>                
                """;

        String cabecalhoHtml = new String(Files.readAllBytes(
                Path.of(getClass().getClassLoader().getResource(
                        "templates/simulado/cabecalho.html").toURI())));

        String titulo = gabarito.getTitulo() != null ? gabarito.getTitulo() : "";
        cabecalhoHtml = cabecalhoHtml.replace("${titulo}", titulo);
        cabecalhoHtml = cabecalhoHtml.replace("${modelo}", "Gabarito");

        StringBuilder container = new StringBuilder("""
                    <br/>
                    <table border="0" width="50%">
                      <tr>        
                """);

        List<GabaritoQuestoesResponse> questoes = gabarito.getQuestoes();
        int controle = 0;

        for (GabaritoQuestoesResponse questao : questoes) {

            if (controle % 25 == 0) {
                if (controle > 0) {
                    container.append("</table></td>"); // Feche a tabela anterior, se houver
                }
                container.append("<td width='20%' valign='top'>")
                        .append("<table width='100%' class='table-bordered'>");

                container.append("<tr><td width='50%'><b>Questão</b></td><td width='50%'><b>Resposta</b></td></tr>");
            }


            container.append("<tr><td width='50%'>")
                    .append(questao.getOrdem())
                    .append("</td><td width='50%'>")
                    .append(Character.toUpperCase(questao.getResposta()))
                    .append("</td></tr>");

            controle++;
        }

        // Verifique se há uma tabela aberta que não foi fechada
        if (controle % 25 != 0) {
            container.append("</table></td>");
        }

        // // Feche a tag <tr> e a tag <table> principais
        container.append("</tr></table></body></html>");

        String htmlContent = container.toString();

        Document header = Jsoup.parse(cabecalhoHtml, "UTF-8", Parser.xmlParser());
        Document content = Jsoup.parse(htmlContent, "UTF-8", Parser.xmlParser());

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            String combinedHtml = start + header.html() + content.html() + "</body></html>";

            builder.withHtmlContent(combinedHtml, new File(".").toURI().toString());
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }

    public byte[] exportarSimuladoParaPDF(String titulo, List<DisciplinaQuestoesResponse> disciplinas)
            throws Exception {

        UsuarioLogado usuario = SecurityUtil.obterUsuarioLogado();

        String instrucoesHtml = new String(Files.readAllBytes(
                Path.of(getClass().getClassLoader().getResource(
                        "templates/simulado/instrucoes.html").toURI())));

        instrucoesHtml = instrucoesHtml.replace("${titulo}", titulo);

        String rascunhoHtml = new String(Files.readAllBytes(
                Path.of(getClass().getClassLoader().getResource(
                        "templates/simulado/rascunho.html").toURI())));


        String startHtml = this.getStart();
        startHtml = startHtml.replace(
                "${nome}", usuario.getNome()).replace(
                "${cpf}", FoxUtils.formatarCpf(usuario.getCpf()));

        Document instrucoes = Jsoup.parse(instrucoesHtml,
                "UTF-8", Parser.xmlParser());
        Document rascunho = Jsoup.parse(rascunhoHtml,
                "UTF-8", Parser.xmlParser());
        Document content = Jsoup.parse(this.getContentHtml(disciplinas),
                "UTF-8", Parser.xmlParser());

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            String combinedHtml = startHtml + instrucoes.html()
                    + rascunho.html() + content.html()
                    + "</body></html>";

            builder.withHtmlContent(combinedHtml, new File(".").toURI().toString());
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
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
                            
                            @page {
                                size: A4;

                                @bottom-center {
                                    content: element(footer);
                                    vertical-align: middle;
                                    margin-bottom: 120px;
                                }
                            }

                            #footer {
                                text-align: center;
                                display: block;
                                position: running(footer);
                            }
                                
                        #titulo-centralizado {
                            width: 100%;
                            display: flex;
                            justify-content: center;
                            background-color: #dadada;
                            padding: 10px 5px;
                            text-align: center;
                            font-size: 14px;
                            font-weight: 600;
                        }                    
                        </style>
                    </head>
                    <body>
                    <div id="footer">
                        <hr style="border: 1px solid #000;"/>
                        ${nome} - CPF: ${cpf}
                    </div>              
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
