package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Banca;
import com.example.demo.dto.DominioDTO;
import com.example.demo.services.BancaService;

@RestController
@RequestMapping(value = "/api/bancas", produces = MediaType.APPLICATION_JSON_VALUE)
public class BancaController {
    
    private final BancaService bancaService;

    public BancaController(BancaService bancaService) {
        this.bancaService = bancaService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@RequestBody DominioDTO dominio) {
        
        Banca banca = new Banca();
        banca.setNome(dominio.getNome());

        banca = bancaService.salvar(banca);
        return ResponseEntity.status(HttpStatus.CREATED).body(banca.getId());
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    @GetMapping(produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Banca>> listar() {
        return ResponseEntity.ok(bancaService.findAll());
    }

}
