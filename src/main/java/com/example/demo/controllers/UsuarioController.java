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


    private final UsuarioServiceImpl respository;

    public UsuarioController(UsuarioServiceImpl respository) {
        this.respository = respository;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> create(@RequestBody Usuario user) {
        UsuarioLogado savedUser = this.respository.save(user);
        return ResponseEntity.ok("Usuário: " + savedUser.getNome() + " criado com sucesso.");
    }

    @PostMapping("/api/usuarios")
    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMINISTRADOR")
    public ResponseEntity<String> save(@RequestBody UsuarioDTO user) {
        return null;
    }

    @DeleteMapping("/api/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(String id) {
        return null;
    }

    @GetMapping("/api/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioDTO>> findAll(Pageable pageable) {
        return null;
    }

    @GetMapping("/api/usuarios/{id}")
    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO') or hasRole('ADMINISTRADOR")
    public ResponseEntity<UsuarioDTO> findById(String id) {
        return null;
    }

}
