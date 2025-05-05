package br.com.foxconcursos.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.services.MatriculaService;

@RestController
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService
    ) {
        this.matriculaService = matriculaService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(path = "/api/admin/matricula",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> matricular(@RequestBody MatriculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.matriculaService.matricular(request));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(path = "/api/admin/remover-matricula",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> desvincular(@RequestBody MatriculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.matriculaService.desvincular(request));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/matricula/alunos-matriculados/{cursoId}")
    public ResponseEntity<Page<Usuario>> buscarMatriculados(@PathVariable UUID cursoId, Pageable pageable) {
        return ResponseEntity.ok(this.matriculaService.buscarUsuariosPorProdutoId(cursoId, pageable));
    }
}
