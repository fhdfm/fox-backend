package com.example.demo.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MatriculaRequest;
import com.example.demo.services.MatriculaService;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    
    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PreAuthorize("hasRole('ROLE_ALUNO') || hasRole('ROLE_EXTERNO')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> matricular(@RequestBody MatriculaRequest request) {
        UUID matriculaId = this.matriculaService.matricular(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaId);
    }

}
