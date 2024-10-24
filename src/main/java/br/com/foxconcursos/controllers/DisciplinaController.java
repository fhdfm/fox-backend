package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.dto.AssuntoResponse;
import br.com.foxconcursos.services.AssuntoService;
import br.com.foxconcursos.services.DisciplinaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class DisciplinaController {

    private final DisciplinaService disciplinaService;
    private final AssuntoService assuntoService;

    public DisciplinaController(
            DisciplinaService disciplinaService,
            AssuntoService assuntoService
    ) {

        this.disciplinaService = disciplinaService;
        this.assuntoService = assuntoService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/disciplinas")
    public ResponseEntity<UUID> salvar(@RequestBody Disciplina disciplina) {
        disciplina = disciplinaService.salvar(disciplina);
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplina.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/disciplinas/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Disciplina> atualizar(@PathVariable UUID id,
                                                @RequestBody Disciplina disciplina) {
        disciplina.setId(id);
        return ResponseEntity.ok(disciplinaService.salvar(disciplina));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/disciplinas/{id}")
    public ResponseEntity<Disciplina> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(disciplinaService.findById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') or hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping({
            "/api/admin/disciplinas",
            "/api/aluno/disciplinas"
    })
    public ResponseEntity<List<Disciplina>> listar(
            @RequestParam(required = false) String filter) throws Exception {

        return ResponseEntity.ok(disciplinaService.findAll(filter));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/disciplinas/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        disciplinaService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Disciplina deletada com sucesso: " + id);
    }


    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/disciplinas/{disciplinaId}/assuntos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarAssuntoPorDisciplina(
            @PathVariable UUID disciplinaId,
            @RequestBody Assunto assunto
    ) {
        assunto = assuntoService.salvar(disciplinaId, assunto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assunto.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/disciplinas/{disciplinaId}/assuntos/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assunto> atualizarAssuntoPorDisciplina(
            @PathVariable UUID disciplinaId,
            @PathVariable UUID id,
            @RequestBody Assunto assunto) {
        assunto.setId(id);
        return ResponseEntity.ok(assuntoService.salvar(disciplinaId, assunto));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/disciplinas/{disciplinaId}/assuntos/{id}")
    public ResponseEntity<Assunto> buscarAssuntoPorDisciplina(
            @PathVariable UUID id,
            @PathVariable UUID disciplinaId
    ) throws Exception {
        return ResponseEntity.ok(assuntoService.findByIdAndDisciplinaId(id, disciplinaId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/disciplinas/{disciplinaId}/assuntos")
    public ResponseEntity<List<Assunto>> buscarTodosAssuntosPorDisciplina(
            @PathVariable UUID disciplinaId, @RequestParam(required = false) String filter
    ) throws Exception {
        return ResponseEntity.ok(assuntoService.findByDisciplinaId(disciplinaId, filter));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') or hasAuthority('SCOPE_ROLE_ALUNO') ")
    @PostMapping({
            "/api/admin/disciplinas/assuntos",
            "/api/aluno/disciplinas/assuntos"
    })
    public ResponseEntity<List<AssuntoResponse>> buscarAssuntosPorDisciplinas(
            @RequestBody List<String> disciplinas
    ) {
        return ResponseEntity.ok(assuntoService.buscarAssuntosPorDisciplinas(disciplinas));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/disciplinas/assuntos/{id}")
    public ResponseEntity<String> deletarAssuntoPorDisciplina(
            @PathVariable UUID id
    ) {
        assuntoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Assunto deletado com sucesso: " + id);
    }
}