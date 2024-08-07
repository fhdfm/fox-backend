package br.com.foxconcursos.controllers;

import java.util.List;
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

import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.GabaritoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoRequest;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.QuestoesSimuladoDisciplinaResponse;
import br.com.foxconcursos.dto.RespostaSimuladoRequest;
import br.com.foxconcursos.dto.ResultadoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.SimuladoRequest;
import br.com.foxconcursos.dto.SimuladoResponse;
import br.com.foxconcursos.dto.SimuladoResumoResponse;
import br.com.foxconcursos.services.PdfService;
import br.com.foxconcursos.services.QuestaoSimuladoService;
import br.com.foxconcursos.services.RespostaSimuladoService;
import br.com.foxconcursos.services.SimuladoService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SimuladoController {
    
    private final SimuladoService simuladoService;
    private final QuestaoSimuladoService questaoSimuladoService;
    private final RespostaSimuladoService respostaSimuladoService;
    private final PdfService pdfService;

    public SimuladoController(SimuladoService simuladoService, 
        QuestaoSimuladoService questaoSimuladoService, 
        RespostaSimuladoService respostaSimuladoService,
        PdfService pdfService) {
        
        this.simuladoService = simuladoService;
        this.questaoSimuladoService = questaoSimuladoService;
        this.respostaSimuladoService = respostaSimuladoService;
        this.pdfService = pdfService;
    }

    // @PostMapping(value = "/simulados/{simuladoId}/data-inicio", 
    //     consumes = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<String> atualizarDataInicio(
    //     @PathVariable UUID simuladoId, @RequestBody LocalDateTime dataInicio) {
    //     simuladoService.updateDataInicio(simuladoId, dataInicio);
    //     return ResponseEntity.status(HttpStatus.OK).body("Data de início atualizada com sucesso.");
    // }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoResponse> update(@PathVariable UUID id, 
        @RequestBody SimuladoRequest request) {
        SimuladoResponse response = simuladoService.save(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        simuladoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body("Simulado deletado com sucesso." + id);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<SimuladoCompletoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(simuladoService.findById(id));
    }

    /* Questões do Simulado */

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes")
    public ResponseEntity<QuestoesSimuladoDisciplinaResponse>
        findBySimuladoId(@PathVariable UUID simuladoId) {
        UUID cursoId = simuladoService.getCursoAssociado(simuladoId);
        return ResponseEntity.ok(
            this.questaoSimuladoService.findBySimuladoId(cursoId, simuladoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}")
    public ResponseEntity<String> deleteQuestao(@PathVariable UUID simuladoId, 
        @PathVariable UUID questaoId) {
        this.questaoSimuladoService.delete(questaoId);
        return ResponseEntity.status(HttpStatus.OK)
            .body("Questão deletada com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}")
    public ResponseEntity<QuestaoSimuladoResponse> findQuestaoById(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId) {
        return ResponseEntity.ok(this.questaoSimuladoService.findById(questaoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/simulados/{simuladoId}/questoes", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarQuestao(@PathVariable UUID simuladoId, 
        @RequestBody QuestaoSimuladoRequest request) {
        UUID id = questaoSimuladoService.save(simuladoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestaoSimuladoResponse> atualizarQuestao(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId,
        @RequestBody QuestaoSimuladoRequest request) {
        
        QuestaoSimuladoResponse response = questaoSimuladoService.save(
            simuladoId, questaoId, request);
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/iniciar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoCompletoResponse> iniciarSimulado(
        @PathVariable UUID simuladoId) {

        respostaSimuladoService.iniciar(simuladoId);

        return ResponseEntity.ok(
            simuladoService.findById(
                simuladoId, false));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @GetMapping(value = "/api/alunos/simulados/{simuladoId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportarSimuladoParaPDF(@PathVariable UUID simuladoId) throws Exception {

        SimuladoCompletoResponse simulado = simuladoService.findById(simuladoId);
        List<DisciplinaQuestoesResponse> disciplinas = simulado.getDisciplinas();

        byte[] pdf = this.pdfService.exportarSimuladoParaPDF(
            simulado.getTitulo(), disciplinas);

        return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Disposition", 
                "attachment; filename=simulado-" + UUID.randomUUID() +  ".pdf").body(pdf);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @GetMapping(value = "/api/alunos/simulados/{simuladoId}/gabarito", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportarGabaritoPataPDF(@PathVariable UUID simuladoId) throws Exception {

        GabaritoResponse gabarito = this.simuladoService.obterGabarito(simuladoId);

        byte[] pdf = this.pdfService.exportarGabaritoParaPDF(gabarito);
        return ResponseEntity.status(HttpStatus.OK).header(
        "attachment; filename=gabarito-" + UUID.randomUUID() +  ".pdf").body(pdf);
    }

        
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @GetMapping(value = "/api/alunos/simulados/{simuladoId}/status")
    public ResponseEntity<String> obterStatus(@PathVariable UUID simuladoId) {

        StatusSimulado status = 
            this.respostaSimuladoService.obterStatus(simuladoId);
        
        return ResponseEntity.status(HttpStatus.OK).body(status.name());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/salvar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@PathVariable UUID simuladoId, 
        @RequestBody RespostaSimuladoRequest respostas) throws Exception {
        
        UUID respostaSimuladoId =
            this.respostaSimuladoService.salvar(simuladoId, respostas);

        return ResponseEntity.status(HttpStatus.OK).body(respostaSimuladoId);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/finalizar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> finalizarSimulado(@PathVariable UUID simuladoId) {
        
        return ResponseEntity.ok(
            this.respostaSimuladoService.finalizar(simuladoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/corrente")
    public ResponseEntity<SimuladoCompletoResponse> obterSimuladoCorrente(
        @PathVariable UUID simuladoId) {
       
        StatusSimulado status =
            this.respostaSimuladoService.obterStatus(simuladoId);     

       SimuladoCompletoResponse response = null;
         if (status == StatusSimulado.FINALIZADO)
           response = this.simuladoService.findById(simuladoId);
        else
           response = this.simuladoService.findById(simuladoId, false);     
       
       response = this.respostaSimuladoService.obterRespostas(
            response);
    
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/ranking-geral")
    public ResponseEntity<ResultadoSimuladoResponse> obterRankingGeral(
        @PathVariable UUID simuladoId) {
        
        return ResponseEntity.status(HttpStatus.OK).body(
            this.respostaSimuladoService.obterRanking(simuladoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(path = "/api/alunos/simulados/{simuladoId}/ranking-individual")
    public ResponseEntity<ResultadoSimuladoResponse> obterRankingIndividual(
        @PathVariable UUID simuladoId) {
                
        return ResponseEntity.status(HttpStatus.OK).body(
            this.respostaSimuladoService.obterRankingPorUsuario(simuladoId));
    }

    @GetMapping(path = "/api/simulados/{simuladoId}/prepare")
    public ResponseEntity<String> prepareCache(@PathVariable UUID simuladoId) {
        simuladoService.prepararSimulado(simuladoId);
        return ResponseEntity.status(HttpStatus.OK).body("Cache preparado com sucesso.");
    }
}
