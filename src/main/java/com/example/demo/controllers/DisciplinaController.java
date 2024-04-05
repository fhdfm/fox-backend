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

import com.example.demo.domain.Disciplina;
import com.example.demo.dto.DominioDTO;
import com.example.demo.services.DisciplinaService;


@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {
 
    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody DominioDTO dominio) {
        
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dominio.getNome());

        disciplina = disciplinaService.salvar(disciplina);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            "Disciplina " + disciplina.getNome() + " criada com sucesso!");
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<Disciplina>> listar() {
        return ResponseEntity.ok(disciplinaService.findAll());
    }

}
