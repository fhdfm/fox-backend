package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/bancas")
public class BancaController {
    
    private final BancaService bancaService;

    public BancaController(BancaService bancaService) {
        this.bancaService = bancaService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> salvar(@RequestBody DominioDTO dominio) {
        
        Banca banca = new Banca();
        banca.setNome(dominio.getNome());

        banca = bancaService.salvar(banca);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            "Banca " + banca.getNome() + " criada com sucesso!");
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    @GetMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Banca>> listar() {
        return ResponseEntity.ok(bancaService.findAll());
    }

}
