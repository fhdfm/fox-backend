package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.UUID;

import br.com.foxconcursos.domain.Simulado;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.CursoDisciplina;
import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.dto.AddDisciplinaRequest;
import br.com.foxconcursos.dto.CursoDTO;
import br.com.foxconcursos.services.CursoService;
import br.com.foxconcursos.services.DisciplinaService;

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

    @PostMapping(value = "/api/admin/cursos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<UUID> save(@RequestBody CursoDTO courseDTO) {

        UUID newCurso = this.cursoService.save(courseDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(newCurso);
    }

    @DeleteMapping(value = "/api/admin/cursos/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        this.cursoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Curso deletado com sucesso.");
    }

    @GetMapping(value = {"/api/cursos", "/api/admin/cursos"})
    public ResponseEntity<Page<CursoDTO>> findAll(@RequestParam(required = false) String filter, Pageable pageable) throws Exception {
        return ResponseEntity.ok(this.cursoService.findAll(pageable, filter));
    }

    @GetMapping(value = "/api/cursos/{id}")
    public ResponseEntity<CursoDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(this.cursoService.findById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/cursos/{id}/simulados")
    public ResponseEntity<List<Simulado>> findSimuladoByCursoId(@PathVariable UUID id) {
        return ResponseEntity.ok(this.cursoService.findSimuladoByCursoId(id));
    }

    @GetMapping(value = "/api/cursos/{cursoId}/disciplinas")
    public ResponseEntity<List<Disciplina>> listarDisciplinasCurso(@PathVariable UUID cursoId) {
        return ResponseEntity.ok(this.disciplinaService.findByCursoId(cursoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/cursos/{cursoId}/disciplinas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> adicionarDisciplinas(@PathVariable UUID cursoId, 
        @RequestBody AddDisciplinaRequest disciplinas) {
        this.disciplinaService.adicionarDisciplinas(cursoId, disciplinas.getIds());
        return ResponseEntity.status(HttpStatus.OK).body("Disciplina adicionada com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/cursos/{cursoId}/disciplinas/{disciplinaId}")
    public ResponseEntity<String> removerDisciplina(@PathVariable UUID cursoId, @PathVariable UUID disciplinaId) {
        CursoDisciplina cursoDisciplina = new CursoDisciplina(cursoId, disciplinaId);
        this.disciplinaService.removerDisciplina(cursoDisciplina);
        return ResponseEntity.status(HttpStatus.OK).body("Disciplina removida com sucesso: " + disciplinaId);
    }
}
