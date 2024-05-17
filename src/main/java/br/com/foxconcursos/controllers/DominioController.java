package br.com.foxconcursos.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Escolaridade;
import br.com.foxconcursos.domain.Status;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DominioController {
    
    @GetMapping(value = "/dominio/escolaridades")
    public ResponseEntity<List<Escolaridade>> listarDominios() {
        return ResponseEntity.ok(Arrays.asList(Escolaridade.values()));
    }
    
    @GetMapping(value = "/dominio/status")
    public ResponseEntity<List<Status>> listarStatus() {
        return ResponseEntity.ok(Arrays.asList(Status.values()));
    }

}
