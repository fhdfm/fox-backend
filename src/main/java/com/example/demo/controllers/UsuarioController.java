package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Usuario;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.UsuarioResponse;
import com.example.demo.services.impl.UsuarioServiceImpl;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {


    private final UsuarioServiceImpl service;

    public UsuarioController(UsuarioServiceImpl service) {
        this.service = service;
    }
    
    @PostMapping(value = "/api/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> create(@RequestBody Usuario user) {
        UsuarioLogado savedUser = this.service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser.getId());
    }

    @DeleteMapping(value = "/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> desativar(@PathVariable UUID id) {
        this.service.desativar(id);
        return ResponseEntity.ok("Usuário desativado com sucesso.");
    }

    @GetMapping(value = "/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> findAll(
        @RequestParam(required = false) String filter) throws Exception {
        
        return ResponseEntity.ok(service.findAll(filter));
    }

    @GetMapping(value = "/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

}
