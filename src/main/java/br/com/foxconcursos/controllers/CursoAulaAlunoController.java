package br.com.foxconcursos.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.dto.CursoAlunoResponse;
import br.com.foxconcursos.services.CursoAlunoService;
import jakarta.websocket.server.PathParam;

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
    public ResponseEntity<String> complete(@PathParam("cursoId") UUID cursoId, @PathParam("aulaId") UUID aulaId) {
        this.service.salvarProgresso(cursoId, aulaId);
        return ResponseEntity.status(HttpStatus.OK).body("Progresso salvo com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/{cursoId}/start")
    public ResponseEntity<CursoAlunoResponse> iniciar(@PathParam("cursoId") UUID cursoId) {
        CursoAlunoResponse response = this.service.obterCurso(cursoId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/{cursoId}/aula/{aulaId}/")
    public ResponseEntity<CursoAlunoResponse> mudarAula(@PathParam("cursoId") UUID cursoId, @PathParam("aulaId") UUID aulaId) {
        CursoAlunoResponse response = this.service.obterCurso(cursoId, aulaId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
