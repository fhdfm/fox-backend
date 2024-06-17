package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;

import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.ItemQuestaoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoRequest;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.QuestoesSimuladoDisciplinaResponse;
import br.com.foxconcursos.dto.RespostaSimuladoRequest;
import br.com.foxconcursos.dto.ResultadoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.SimuladoRequest;
import br.com.foxconcursos.dto.SimuladoResponse;
import br.com.foxconcursos.dto.SimuladoResumoResponse;
import br.com.foxconcursos.services.AuthenticationService;
import br.com.foxconcursos.services.QuestaoSimuladoService;
import br.com.foxconcursos.services.RespostaSimuladoService;
import br.com.foxconcursos.services.SimuladoService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SimuladoController {
    
    private final SimuladoService simuladoService;
    private final QuestaoSimuladoService questaoSimuladoService;
    private final RespostaSimuladoService respostaSimuladoService;
    private final AuthenticationService authenticationService;

    public SimuladoController(SimuladoService simuladoService, 
        QuestaoSimuladoService questaoSimuladoService, 
        RespostaSimuladoService respostaSimuladoService,
        AuthenticationService authenticationService) {
        
        this.simuladoService = simuladoService;
        this.questaoSimuladoService = questaoSimuladoService;
        this.respostaSimuladoService = respostaSimuladoService;
        this.authenticationService = authenticationService;

    }

    // @PostMapping(value = "/simulados/{simuladoId}/data-inicio", 
    //     consumes = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<String> atualizarDataInicio(
    //     @PathVariable UUID simuladoId, @RequestBody LocalDateTime dataInicio) {
    //     simuladoService.updateDataInicio(simuladoId, dataInicio);
    //     return ResponseEntity.status(HttpStatus.OK).body("Data de início atualizada com sucesso.");
    // }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/simulados", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> save(@RequestBody SimuladoRequest request) {
        UUID id = simuladoService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping(value = "/api/simulados")
    public ResponseEntity<List<SimuladoResumoResponse>> findAll(
        @RequestParam(required = false) String filter) throws Exception {
        if (filter != null) {
            return ResponseEntity.ok(simuladoService.findByExample(filter));
        }        
        return ResponseEntity.ok(simuladoService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoResponse> update(@PathVariable UUID id, 
        @RequestBody SimuladoRequest request) {
        SimuladoResponse response = simuladoService.save(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        simuladoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body("Simulado deletado com sucesso." + id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<SimuladoCompletoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(simuladoService.findById(id));
    }

    /* Questões do Simulado */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes")
    public ResponseEntity<QuestoesSimuladoDisciplinaResponse>
        findBySimuladoId(@PathVariable UUID simuladoId) {
        UUID cursoId = simuladoService.getCursoAssociado(simuladoId);
        return ResponseEntity.ok(
            this.questaoSimuladoService.findBySimuladoId(cursoId, simuladoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}")
    public ResponseEntity<String> deleteQuestao(@PathVariable UUID simuladoId, 
        @PathVariable UUID questaoId) {
        this.questaoSimuladoService.delete(questaoId);
        simuladoService.decrementarQuestoes(simuladoId);
        return ResponseEntity.status(HttpStatus.OK)
            .body("Questão deletada com sucesso.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}")
    public ResponseEntity<QuestaoSimuladoResponse> findQuestaoById(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId) {
        return ResponseEntity.ok(this.questaoSimuladoService.findById(questaoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/simulados/{simuladoId}/questoes", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarQuestao(@PathVariable UUID simuladoId, 
        @RequestBody QuestaoSimuladoRequest request) {
        UUID id = questaoSimuladoService.save(simuladoId, request);
        simuladoService.incrementarQuestoes(simuladoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestaoSimuladoResponse> atualizarQuestao(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId,
        @RequestBody QuestaoSimuladoRequest request) {
        
        QuestaoSimuladoResponse response = questaoSimuladoService.save(
            simuladoId, questaoId, request);
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/iniciar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoCompletoResponse> iniciarSimulado(
        @PathVariable UUID simuladoId) {
        
        UUID usuarioId = this.authenticationService.obterUsuarioLogado();

        respostaSimuladoService.iniciar(simuladoId, usuarioId);

        return ResponseEntity.ok(
            simuladoService.findById(
                simuladoId, false));
    }

    @GetMapping(value = "/api/pf", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoCompletoResponse> xyuayaio(
        @PathVariable UUID simuladoId) {
        
        //UUID usuarioId = UUID.fromString("3d0f0f05-4506-40cd-98c5-4d7911f6d4ff");

        //respostaSimuladoService.iniciar(simuladoId, usuarioId);

        return ResponseEntity.ok(
            simuladoService.findById(
                simuladoId, false));
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @GetMapping(value = "/api/alunos/simulados/{simuladoId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportarSimuladoParaPDF(@PathVariable UUID simuladoId) throws Exception {

        SimuladoCompletoResponse simulado = simuladoService.findById(simuladoId);
        List<DisciplinaQuestoesResponse> disciplinasDS = simulado.getDisciplinas();

        UsuarioLogado usuario = this.authenticationService.obterUsuarioLogadoCompleto();
	
        
        // InputStream logo = new ClassPathResource("logo-fox.png").getInputStream();

        // JRBeanCollectionDataSource disciplinas = new JRBeanCollectionDataSource(disciplinasDS);
        // Map<String, Object> parameters = new HashMap<String, Object>();
        // parameters.put("nome", usuario.getNome());
        // parameters.put("cpf", usuario.getCpf());
        // parameters.put("titulo", simulado.getTitulo());
        // parameters.put("logo", logo);

        // try (InputStream jasperStream = jasperResource.getInputStream()) {
        //    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parameters, disciplinas);
        //    byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
        //    HttpHeaders headers = new HttpHeaders();
        //    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=simulado_" 
        //     + usuario.getCpf() + ".pdf");
        //    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("""
            <html>
            <head>
<style>
    * {
        font-family: Arial, Helvetica, sans-serif;
    }
    .abc {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: flex-start; /* Alinha os itens à esquerda */
    }
    .abc span {
        display: block; /* Define os spans como blocos para ficarem um abaixo do outro */
        margin: 0 0 10px 0;  /* Adiciona espaço entre os spans */
    }
    .header-container {
        margin: 0 0 10px 0;
        display: flex;
        align-items: center; /* Alinha verticalmente os itens ao centro */
    }
    .header_titulo {
        font-size: 18px;
        margin: 0 10px 0 0; /* Adiciona espaço entre os spans */
        font-weight: 600;
        display: inline; /* Garante que os spans fiquem na mesma linha */
    }
    .disc {
        background-color: #f1f1f1;
        padding: 15px 10px;
    }
    .question-container {
        margin-bottom: 20px;
    }
    .question-text {
        display: inline; /* Garante que o texto da questão esteja na mesma linha */
    }
    .question-number {
        display: inline; /* Garante que o número da questão esteja na mesma linha */
        font-weight: bold;
    }
    .options {
        display: flex;
        flex-direction: column;
        margin-top: 10px;
    }
</style>

<meta charset='utf-8'>
            </head>
            <body>
       
            """);
        
            for (DisciplinaQuestoesResponse disciplina : disciplinasDS) {
                if (disciplina.getQuestoes().isEmpty()) continue;
            
                htmlContent.append("<h3 class='disc'>")
                           .append(disciplina.getNome())
                           .append("</h3>");
            
                for (QuestaoSimuladoResponse questao : disciplina.getQuestoes()) {
                    htmlContent.append("<div class='question-container'>")
                        .append("<span class='question-text'>")
                        .append(questao.getOrdem() + ") " + questao.getEnunciado())
                        .append("</span>")
                        .append("</div>");
            
                    htmlContent.append("<div class='abc'>");
                    for (ItemQuestaoResponse alternativa : questao.getAlternativas()) {
                        htmlContent.append("<span>")
                            .append(alternativa.getOrdem() + ") " + alternativa.getDescricao())
                            .append("</span>");
                    }
                    htmlContent.append("</div>");
                }
            }
            
          
          
          htmlContent.append("</body></html>");
        
        String finalHtmlContent = htmlContent.toString();

        System.out.println(finalHtmlContent);
        

        String rascunho = """
<!DOCTYPE html>
<html lang="en'>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <style>
      * {
        font-family: Arial, Helvetica, sans-serif;
      }
      .rascunho {
        width: 21cm; /* Largura de uma folha A4 */
        height: 29.7cm; /* Altura de uma folha A4 */
        margin: 0 auto;
        position: relative; /* Para o posicionamento absoluto do texto */
        box-sizing: border-box;
      }
      .rotated-text {
        font-size: 150px;
        color: rgb(211, 211, 211);
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%) rotate(-45deg);
        -webkit-transform: translate(-50%, -50%) rotate(-45deg); /* Prefixo WebKit */
        -moz-transform: translate(-50%, -50%) rotate(-45deg); /* Prefixo Mozilla */
        -ms-transform: translate(-50%, -50%) rotate(-45deg); /* Prefixo Microsoft */
        -o-transform: translate(-50%, -50%) rotate(-45deg); /* Prefixo Opera */
        display: inline-block;
      }
    </style>
  </head>
  <body>
    <div class='rascunho'>
      <span class='rotated-text'>Rascunho</span>
    </div>
  </body>
</html>
""";	        

String headerHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8'>
  <style>
    .header-content {
      text-align: center;
      font-size: 12px;
    }
    .hidden {
      display: none;
    }
  </style>
</head>
<body>
  <div class='header-content" id="header-content'>
    Cabeçalho Repetido a partir da Página 3
  </div>
  <script>
    // Script to hide header on first two pages
    if (typeof window !== 'undefined') {
      var headerContent = document.getElementById('header-content');
      window.onload = function() {
        var currentPage = window.location.href.match(/page=(\\d+)/);
        if (currentPage && parseInt(currentPage[1], 10) < 3) {
          headerContent.classList.add('hidden');
        }
      }
    }
  </script>
</body>
</html>
""";

        String executable = WrapperConfig.findExecutable();

        Pdf pdf = new Pdf(new WrapperConfig(executable));
        pdf.cleanAllTempFiles();

        //pdf.addParam(new Param("--header-html", headerHtml));
        pdf.addParam(new Param("--disable-forms"));        
        pdf.addParam(new Param("--margin-top", "20mm"));
        pdf.addParam(new Param("--margin-bottom", "20mm"));
        pdf.addParam(new Param("--margin-left", "10mm"));
        pdf.addParam(new Param("--margin-right", "10mm"));

        String instrucoesContent = new Scanner(getClass().getClassLoader().getResourceAsStream(
            "instrucoes.html"), "UTF-8").useDelimiter("\\A").next();
        instrucoesContent = instrucoesContent.replace("$nome", usuario.getNome());
        instrucoesContent = instrucoesContent.replace("$cpf", usuario.getCpf());
        instrucoesContent = instrucoesContent.replace("$titulo", simulado.getTitulo());

        // Add a single page with dynamic content
        Page page1 = pdf.addPageFromString(instrucoesContent);
        Page page2 = pdf.addPageFromString(rascunho);
        Page page3 = pdf.addPageFromString(finalHtmlContent);

        // Save the PDF
        pdf.saveAs("output.pdf");        

        return null;
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @GetMapping(value = "/api/alunos/simulados/{simuladoId}/status")
    public ResponseEntity<String> obterStatus(@PathVariable UUID simuladoId) {

        UUID usuarioId = this.authenticationService.obterUsuarioLogado();

        StatusSimulado status = 
            this.respostaSimuladoService.obterStatus(simuladoId, usuarioId);
        
        return ResponseEntity.status(HttpStatus.OK).body(status.name());
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/salvar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@PathVariable UUID simuladoId, 
        @RequestBody RespostaSimuladoRequest respostas) throws Exception {

        UUID usuarioId = this.authenticationService.obterUsuarioLogado();
        
        UUID respostaSimuladoId =
            this.respostaSimuladoService.salvar(
                simuladoId, usuarioId, respostas);

        return ResponseEntity.status(HttpStatus.OK).body(respostaSimuladoId);
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/finalizar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> finalizarSimulado(@PathVariable UUID simuladoId) {
        
        UUID usuarioId = this.authenticationService.obterUsuarioLogado();

        return ResponseEntity.ok(this.respostaSimuladoService.finalizar(
            simuladoId, usuarioId));
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/corrente")
    public ResponseEntity<SimuladoCompletoResponse> obterSimuladoCorrente(
        @PathVariable UUID simuladoId) {
       
        UUID usuarioId = this.authenticationService.obterUsuarioLogado();

        StatusSimulado status =
            this.respostaSimuladoService.obterStatus(
                simuladoId, usuarioId);     

       SimuladoCompletoResponse response = null;
         if (status == StatusSimulado.FINALIZADO)
           response = this.simuladoService.findById(simuladoId);
        else
           response = this.simuladoService.findById(simuladoId, false);     
       
       response = this.respostaSimuladoService.obterRespostas(
            response, usuarioId);
    
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/ranking-geral")
    public ResponseEntity<ResultadoSimuladoResponse> obterRankingGeral(
        @PathVariable UUID simuladoId) {
        
        return ResponseEntity.status(HttpStatus.OK).body(
            this.respostaSimuladoService.obterRanking(simuladoId));
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/ranking-individual")
    public ResponseEntity<ResultadoSimuladoResponse> obterRankingIndividual(
        @PathVariable UUID simuladoId) {
        
        UUID usuarioId = this.authenticationService.obterUsuarioLogado();
        
        return ResponseEntity.status(HttpStatus.OK).body(
            this.respostaSimuladoService.obterRanking(simuladoId, usuarioId));
    }
}
