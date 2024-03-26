package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.CurrentUser;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDTO;
import com.example.demo.services.impl.UserDetailsServiceImpl;

@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserDetailsServiceImpl respository;

    public UserController(UserDetailsServiceImpl respository) {
        this.respository = respository;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> create(@RequestBody User user) {
        CurrentUser savedUser = this.respository.save(user);
        return ResponseEntity.ok("Usuário: " + savedUser.getName() + " criado com sucesso.");
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> save(@RequestBody UserDTO user) {
        return null;
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(String id) {
        return null;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        return null;
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> findById(String id) {
        return null;
    }

}
