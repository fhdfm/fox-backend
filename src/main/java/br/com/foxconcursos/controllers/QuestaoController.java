package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.FiltroQuestao;
import br.com.foxconcursos.dto.*;
import br.com.foxconcursos.services.ComentarioService;
import br.com.foxconcursos.services.PdfService;
import br.com.foxconcursos.services.QuestaoService;
import br.com.foxconcursos.services.RespostaService;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestaoController {

    private final ComentarioService comentarioService;
    private final QuestaoService questaoService;
    private final RespostaService respostaService;
    private final PdfService pdfService;

    public QuestaoController(QuestaoService questaoService,
                             PdfService pdfService, ComentarioService comentarioService,
                             RespostaService respostaService) {

        this.questaoService = questaoService;
        this.pdfService = pdfService;
        this.comentarioService = comentarioService;
        this.respostaService = respostaService;

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/questoes")
    public ResponseEntity<UUID> salvar(@RequestBody QuestaoRequest request) {

        UUID id = this.questaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping("/api/admin/questoes/{id}")
    public ResponseEntity<String> atualizar(@RequestBody QuestaoRequest request,
                                            @PathVariable UUID id) {

        this.questaoService.update(request, id);
        return ResponseEntity.status(HttpStatus.OK).body("Questao: " + id
                + " atualizada com sucesso.");

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/questoes/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {

        this.questaoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Questao: "
                + id + " deletada com sucesso.");

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping({"/api/admin/questoes/{questaoId}/comentarios/{comentarioId}",
            "/api/aluno/questoes/{questaoId}/comentarios/{comentarioId}"})
    public ResponseEntity<String> deletarComentario(
            @PathVariable UUID questaoId, @PathVariable UUID comentarioId) {
        comentarioService.delete(comentarioId);
        return ResponseEntity.status(HttpStatus.OK).body(
                "Comentario deletado com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping({
            "/api/admin/questoes/{questaoId}/comentarios",
            "/api/aluno/questoes/{questaoId}/comentarios"
    })
    public ResponseEntity<String> postarComentario(
            @PathVariable UUID questaoId, @RequestBody ComentarioRequest request) {
        comentarioService.save(request, questaoId);
        return ResponseEntity.status(HttpStatus.OK).body(
                "Comentario deletado com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') or hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping(
            {"/api/admin/questoes",
                    "/api/aluno/questoes"}
    )
    public ResponseEntity<BancoQuestaoResponse> findAll(@RequestParam(required = false) String filter,
                                                        @RequestParam(required = true, defaultValue = "25") Integer limit,
                                                        @RequestParam(required = true, defaultValue = "0") Integer offset) throws Exception {

        FiltroQuestao questao = new FiltroQuestao();

        if (filter != null) {
            questao = FoxUtils.criarObjetoDinamico(filter, FiltroQuestao.class);
        }

        int quantidadeRegistros = this.questaoService.getRecordCount(questao);
        int quantidadeDePaginas = quantidadeRegistros / limit;

        BancoQuestaoResponse response = new BancoQuestaoResponse();
        response.setTotalDeRegistros(quantidadeRegistros);
        response.setTotalDePaginas(quantidadeDePaginas);

        List<QuestaoResponse> questoes =
                this.questaoService.findAll(questao, limit, offset);

        response.setQuestoes(questoes);

        Map<String, String> filtro = this.questaoService.getFiltroCorrente(questao);

        response.setFiltros(filtro);

        response.setPerfil(SecurityUtil.obterUsuarioLogado().getPerfil().name());

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/questoes/download")
    public ResponseEntity<byte[]> download(@RequestParam(required = false) String filter,
                                           @RequestParam(required = true, defaultValue = "25") Integer limit,
                                           @RequestParam(required = false, defaultValue = "0") Integer offset) throws Exception {

        FiltroQuestao questao = new FiltroQuestao();

        if (filter != null) {
            questao = FoxUtils.criarObjetoDinamico(filter, FiltroQuestao.class);
        }

        int quantidadeRegistros = this.questaoService.getRecordCount(questao);
        int quantidadeDePaginas = quantidadeRegistros / limit;

        BancoQuestaoResponse response = new BancoQuestaoResponse();
        response.setTotalDeRegistros(quantidadeRegistros);
        response.setTotalDePaginas(quantidadeDePaginas);

        List<QuestaoResponse> questoes =
                this.questaoService.findAll(questao, limit, offset);

        response.setQuestoes(questoes);

        Map<String, String> filtro = this.questaoService.getFiltroCorrente(questao);

        response.setFiltros(filtro);

        byte[] pdf = this.pdfService.gerarPdfFromBancoDeQuestoes(response);

        return ResponseEntity.status(HttpStatus.OK).header(
                "attachment; filename=banco-questoes-" + UUID.randomUUID() + ".pdf").body(pdf);
    }

    @PreAuthorize(" hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/questoes/{id}")
    public ResponseEntity<QuestaoResponse> findById(@PathVariable UUID id) {
        QuestaoResponse questao = this.questaoService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(questao);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @PostMapping("/api/aluno/questoes/{questaoId}/responder")
    public ResponseEntity<ResultadoResponse> responder(@RequestBody RespostaRequest request,
                                                       @PathVariable UUID questaoId) {
        ResultadoResponse uuid = respostaService.save(request, questaoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping({"/api/aluno/questoes/{questaoId}/comentarios", "/api/admin/questoes/{questaoId}/comentarios"})
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(
            @PathVariable("questaoId") UUID questaoId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                comentarioService.findByQuestaoId(questaoId));
    }
}
