package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.dto.CursoAlunoResponse;
import br.com.foxconcursos.services.CursoAlunoService;

@RestController
@RequestMapping(value = "/api/alunos/curso", 
    consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
public class CursoAulaAlunoController {
    
    private CursoAlunoService service;
    
    public CursoAulaAlunoController(CursoAlunoService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/{cursoId}/aula/{aulaId}/complete")
    public ResponseEntity<String> complete(@PathVariable("cursoId") UUID cursoId, @PathVariable("aulaId") UUID aulaId) {
        this.service.salvarProgresso(cursoId, aulaId);
        return ResponseEntity.status(HttpStatus.OK).body("Progresso salvo com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/{cursoId}/start")
    public ResponseEntity<CursoAlunoResponse> iniciar(@PathVariable("cursoId") UUID cursoId) throws IOException {
        CursoAlunoResponse response = this.service.obterCurso(cursoId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/{cursoId}/aula/{aulaId}")
    public ResponseEntity<CursoAlunoResponse> mudarAula(@PathVariable("cursoId") UUID cursoId, @PathVariable("aulaId") UUID aulaId) throws IOException {
        CursoAlunoResponse response = this.service.obterCurso(cursoId, aulaId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
