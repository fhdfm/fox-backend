package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Banca;
import com.example.demo.services.BancaService;

@RestController
@RequestMapping(value = "/api/admin/bancas", produces = MediaType.APPLICATION_JSON_VALUE)
public class BancaController {
    
    private final BancaService bancaService;

    public BancaController(BancaService bancaService) {
        this.bancaService = bancaService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@RequestBody Banca banca) {
        banca = bancaService.salvar(banca);
        return ResponseEntity.status(HttpStatus.CREATED).body(banca.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Banca> buscar(UUID id) {
        return ResponseEntity.ok(bancaService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Banca> atualizar(UUID id, Banca banca) {
        banca.setId(id);
        return ResponseEntity.ok(bancaService.salvar(banca));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Banca>> listar(@RequestParam(required = false) String filter) throws Exception {
        if (filter != null) {
            return ResponseEntity.ok(bancaService.findByExample(filter));
        }
        return ResponseEntity.ok(bancaService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletar(UUID id) {
        bancaService.delete(id);
        return ResponseEntity.ok("Banca deletada: " + id);
    }

}
