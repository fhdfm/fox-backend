package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.CursoDisciplina;
import com.example.demo.domain.Disciplina;
import com.example.demo.dto.CursoDTO;
import com.example.demo.services.CursoService;
import com.example.demo.services.DisciplinaService;

@RestController
public class CursoController {

    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;

    public CursoController(CursoService cursoService,
        DisciplinaService disciplinaService) {
        this.cursoService = cursoService;
        this.disciplinaService = disciplinaService;
    }

    @PostMapping(value = "/api/cursos", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> save(@RequestBody CursoDTO courseDTO) {

        this.cursoService.save(courseDTO);
        return new ResponseEntity<String>(
                "Curso criado com sucesso.", HttpStatus.CREATED);

    }

    @DeleteMapping(value = "/api/cursos/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        this.cursoService.delete(id);
        return ResponseEntity.ok("Curso deletado com sucesso.");
    }

    @GetMapping(value = "/cursos", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Page<CursoDTO>> findAll(Pageable pageable) {

        return new ResponseEntity<Page<CursoDTO>>(
                    this.cursoService.findAll(pageable), HttpStatus.OK);
       
    }

    @GetMapping(value = "/cursos/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CursoDTO> findById(@PathVariable UUID id) {

        return new ResponseEntity<CursoDTO>(
                this.cursoService.findById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/cursos/{cursoId}/disciplinas", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Disciplina>> listarDisciplinasCurso(@PathVariable UUID cursoId) {
        return ResponseEntity.ok(this.disciplinaService.findByCursoId(cursoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/cursos/{cursoId}/disciplinas:add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> adicionarDisciplina(@PathVariable UUID cursoId, @RequestBody Disciplina disciplina) {
        CursoDisciplina cursoDisciplina = new CursoDisciplina(cursoId, disciplina.getId());
        this.disciplinaService.adicionarDisciplina(cursoDisciplina);
        return ResponseEntity.ok("Disciplina adicionada com sucesso.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/cursos/{idCurso}/disciplinas/{idDisciplina}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> removerDisciplina(@PathVariable UUID idCurso, @PathVariable UUID idDisciplina) {
        CursoDisciplina cursoDisciplina = new CursoDisciplina(idCurso, idDisciplina);
        this.disciplinaService.removerDisciplina(cursoDisciplina);
        return ResponseEntity.ok("Disciplina removida com sucesso.");
    }
}
