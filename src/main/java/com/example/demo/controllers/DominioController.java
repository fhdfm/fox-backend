package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Escolaridade;
import com.example.demo.domain.Status;

@RestController
public class DominioController {
    
    @GetMapping("/dominio/escolaridades")
    public ResponseEntity<Escolaridade[]> listarDominios() {
        return ResponseEntity.ok(Escolaridade.values());
    }
    
    @GetMapping("/dominio/status")
    public ResponseEntity<Status[]> listarStatus() {
        return ResponseEntity.ok(Status.values());
    }

}
