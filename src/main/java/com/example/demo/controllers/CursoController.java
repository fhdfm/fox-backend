package com.example.demo.controllers;

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

import com.example.demo.dto.CursoDTO;
import com.example.demo.services.CursoService;

@RestController
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping("/api/cursos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> save(@RequestBody CursoDTO courseDTO) {

        this.cursoService.save(courseDTO);
        return new ResponseEntity<String>(
                "Curso criado com sucesso.", HttpStatus.CREATED);

    }

    @DeleteMapping("/api/cursos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        this.cursoService.delete(id);
        return ResponseEntity.ok("Curso deletado com sucesso.");
    }

    @GetMapping("/cursos")
    public ResponseEntity<Page<CursoDTO>> findAll(Pageable pageable) {

        return new ResponseEntity<Page<CursoDTO>>(
                    this.cursoService.findAll(pageable), HttpStatus.OK);
       
    }

    @GetMapping("/cursos/{id}")
    public ResponseEntity<CursoDTO> findById(@PathVariable UUID id) {

        return new ResponseEntity<CursoDTO>(
                this.cursoService.findById(id), HttpStatus.OK);
    }
}
