package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.CursoDisciplina;
import com.example.demo.domain.Disciplina;
import com.example.demo.dto.AddDisciplinaRequest;
import com.example.demo.dto.CursoDTO;
import com.example.demo.services.CursoService;
import com.example.demo.services.DisciplinaService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CursoController {

    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;

    public CursoController(CursoService cursoService,
        DisciplinaService disciplinaService) {
        this.cursoService = cursoService;
        this.disciplinaService = disciplinaService;
    }

    @PostMapping(value = "/api/cursos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> save(@RequestBody CursoDTO courseDTO) {

        UUID newCurso = this.cursoService.save(courseDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(newCurso);
    }

    @DeleteMapping(value = "/api/cursos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        this.cursoService.delete(id);
        return ResponseEntity.ok("Curso deletado com sucesso.");
    }

    @GetMapping(value = "/cursos")
    public ResponseEntity<Page<CursoDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.cursoService.findAll(pageable));
    }

    @GetMapping(value = "/cursos/{id}")
    public ResponseEntity<CursoDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(this.cursoService.findById(id));
    }

    @GetMapping(value = "/cursos/{cursoId}/disciplinas")
    public ResponseEntity<List<Disciplina>> listarDisciplinasCurso(@PathVariable UUID cursoId) {
        return ResponseEntity.ok(this.disciplinaService.findByCursoId(cursoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/cursos/{cursoId}/disciplinas:add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> adicionarDisciplina(@PathVariable UUID cursoId, 
        @RequestBody AddDisciplinaRequest disciplinas) {
        this.disciplinaService.adicionarDisciplina(cursoId, disciplinas.getIds());
        return ResponseEntity.ok("Disciplina(s) adicionada(s) com sucesso.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/cursos/{cursoId}/disciplinas/{disciplinaId}")
    public ResponseEntity<String> removerDisciplina(@PathVariable UUID cursoId, @PathVariable UUID disciplinaId) {
        CursoDisciplina cursoDisciplina = new CursoDisciplina(cursoId, disciplinaId);
        this.disciplinaService.removerDisciplina(cursoDisciplina);
        return ResponseEntity.ok("Disciplina removida com sucesso.");
    }
}
