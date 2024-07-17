package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Recurso;
import br.com.foxconcursos.dto.AbrirRecursoRequest;
import br.com.foxconcursos.dto.Recurso01Response;
import br.com.foxconcursos.services.AuthenticationService;
import br.com.foxconcursos.services.RecursoService;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class RecursoController {
    
    private final RecursoService recursoService;
    private final AuthenticationService authenticationService;

    public RecursoController(RecursoService recursoService, 
        AuthenticationService authenticationService) {
        
        this.recursoService = recursoService;
        this.authenticationService = authenticationService;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/recursos")
    public ResponseEntity<List<Recurso>> findAll() {
        List<Recurso> recursos = recursoService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(recursos);
    }

    @PreAuthorize("hasRole('ROLE_ALUNO') or hasRole('ROLE_EXTERNO')")
    @GetMapping("/api/alunos/recursos")
    public ResponseEntity<List<Recurso>> findByUsuarioId() {
        UUID usuarioId = authenticationService.obterUsuarioLogado();
        List<Recurso> recursos = recursoService.findByUsuarioId(usuarioId);
        return ResponseEntity.status(HttpStatus.OK).body(recursos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/recursos/usuario/{usuarioId}")
    public ResponseEntity<List<Recurso>> findByUsuarioId(@PathVariable UUID usuarioId) {
        List<Recurso> recursos = recursoService.findByUsuarioId(usuarioId);
        return ResponseEntity.status(HttpStatus.OK).body(recursos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/recursos/simulado/{simuladoId}")
    public ResponseEntity<List<Recurso>> findBySimuladoId(@PathVariable UUID simuladoId) {
        List<Recurso> recursos = recursoService.findBySimuladoId(simuladoId);
        return ResponseEntity.status(HttpStatus.OK).body(recursos); 
    }

    @PreAuthorize("hasRole('ROLE_ALUNO') or hasRole('ROLE_EXTERNO')")
    @PostMapping("/api/alunos/recursos")
    public ResponseEntity<UUID> abrirRecurso(@RequestBody AbrirRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            recursoService.abrirRecurso(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/recursos/{recursoId}")
    public ResponseEntity<Recurso01Response> findById(@PathVariable UUID recursoId) {
        Recurso01Response recurso = recursoService.findById(recursoId);
        return ResponseEntity.status(HttpStatus.OK).body(recurso);
    }

}
