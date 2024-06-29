package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.domain.Disciplina;
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
    @PostMapping(value = "/assunto",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarAssunto(@RequestBody Assunto assunto) {
        assunto = assuntoService.salvar(assunto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assunto.getId());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/assunto/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assunto> atualizarAssunto(@PathVariable UUID id,
                                             @RequestBody Assunto assunto) {
        assunto.setId(id);
        return ResponseEntity.ok(assuntoService.salvar(assunto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/assunto/{id}")
    public ResponseEntity<Assunto> buscarAssunto(@PathVariable UUID id) {
        return ResponseEntity.ok(assuntoService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/assunto")
    public ResponseEntity<List<Assunto>> listarAssunto(
            @RequestParam(required = false) String filter) throws Exception {
        return ResponseEntity.ok(assuntoService.findAll(filter));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/assunto/{id}")
    public ResponseEntity<String> deletarAssunto(@PathVariable UUID id) {
        assuntoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Assunto deletado com sucesso: " + id);
    }
}