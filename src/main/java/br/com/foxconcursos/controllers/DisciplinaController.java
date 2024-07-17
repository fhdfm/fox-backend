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

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.dto.AssuntoResponse;
import br.com.foxconcursos.services.AssuntoService;
import br.com.foxconcursos.services.DisciplinaService;


@RestController
@RequestMapping(value = "/api/admin/disciplinas", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@RequestBody Disciplina disciplina) {
        disciplina = disciplinaService.salvar(disciplina);
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplina.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Disciplina> atualizar(@PathVariable UUID id,
                                                @RequestBody Disciplina disciplina) {
        disciplina.setId(id);
        return ResponseEntity.ok(disciplinaService.salvar(disciplina));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Disciplina> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(disciplinaService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Disciplina>> listar(
            @RequestParam(required = false) String filter) throws Exception {

        return ResponseEntity.ok(disciplinaService.findAll(filter));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        disciplinaService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Disciplina deletada com sucesso: " + id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{disciplinaId}/assuntos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarAssuntoPorDisciplina(
            @PathVariable UUID disciplinaId,
            @RequestBody Assunto assunto
    ) {
        assunto = assuntoService.salvar(disciplinaId, assunto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assunto.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{disciplinaId}/assuntos/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assunto> atualizarAssuntoPorDisciplina(
            @PathVariable UUID disciplinaId,
            @PathVariable UUID id,
            @RequestBody Assunto assunto) {
        assunto.setId(id);
        return ResponseEntity.ok(assuntoService.salvar(disciplinaId, assunto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{disciplinaId}/assuntos/{id}")
    public ResponseEntity<Assunto> buscarAssuntoPorDisciplina(
            @PathVariable UUID id,
            @PathVariable UUID disciplinaId
    ) throws Exception {
        return ResponseEntity.ok(assuntoService.findByIdAndDisciplinaId(id, disciplinaId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{disciplinaId}/assuntos")
    public ResponseEntity<List<Assunto>> buscarTodosAssuntosPorDisciplina(
            @PathVariable UUID disciplinaId, @RequestParam(required = false) String filter
    ) throws Exception {
        return ResponseEntity.ok(assuntoService.findByDisciplinaId(disciplinaId,filter));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/assuntos")
    public ResponseEntity<List<AssuntoResponse>> buscarAssuntosPorDisciplinas(
            @RequestBody List<String> disciplinas
    ) {
        return ResponseEntity.ok(assuntoService.buscarAssuntosPorDisciplinas(disciplinas));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/assuntos/{id}")
    public ResponseEntity<String> deletarAssuntoPorDisciplina(
            @PathVariable UUID id
    ) {
        assuntoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Assunto deletado com sucesso: " + id);
    }
}