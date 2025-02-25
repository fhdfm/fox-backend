package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.dto.MercadoPagoRequest;
import br.com.foxconcursos.services.MatriculaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        UUID matriculaId = this.matriculaService.matricular(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaId);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/matricula/alunos-matriculados/{cursoId}")
    public ResponseEntity<Page<Usuario>> buscarMatriculados(@PathVariable UUID cursoId, Pageable pageable) {
        return ResponseEntity.ok(this.matriculaService.buscarUsuariosPorProdutoId(cursoId, pageable));
    }
}
