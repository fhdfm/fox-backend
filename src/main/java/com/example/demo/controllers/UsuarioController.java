package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Usuario;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.services.impl.UsuarioServiceImpl;

@RestController
public class UsuarioController {


    private final UsuarioServiceImpl service;

    public UsuarioController(UsuarioServiceImpl service) {
        this.service = service;
    }
    
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> create(@RequestBody Usuario user) {
        UsuarioLogado savedUser = this.service.save(user);
        return ResponseEntity.ok("Usuário: " + savedUser.getNome() + " criado com sucesso.");
    }

    @PostMapping(value = "/api/usuarios", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    public ResponseEntity<String> save(@RequestBody UsuarioDTO user) {
        return null;
    }

    @DeleteMapping(value = "/api/usuarios/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(String id) {
        return null;
    }

    @GetMapping(value = "/api/usuarios", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioDTO>> findAll(Pageable pageable) {
        return null;
    }

    @GetMapping(value = "/api/usuarios/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> findById(String id) {
        return null;
    }

}
