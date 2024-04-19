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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.QuestaoSimuladoDTO;
import com.example.demo.dto.SimuladoComQuestoesResponse;
import com.example.demo.dto.SimuladoDTO;
import com.example.demo.dto.SimuladoRequest;
import com.example.demo.dto.SimuladoResponse;
import com.example.demo.services.QuestaoSimuladoService;
import com.example.demo.services.SimuladoService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SimuladoController {
    
    private final SimuladoService simuladoService;
    private final QuestaoSimuladoService questaoSimuladoService;

    public SimuladoController(SimuladoService simuladoService, 
        QuestaoSimuladoService questaoSimuladoService) {
        this.simuladoService = simuladoService;
        this.questaoSimuladoService = questaoSimuladoService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/simulados", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> save(@RequestBody SimuladoRequest request) {
        UUID id = simuladoService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoResponse> update(@PathVariable UUID id, 
        @RequestBody SimuladoRequest request) {
        SimuladoResponse response = simuladoService.save(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        simuladoService.delete(id);
        return ResponseEntity.ok("Simulado deletado com sucesso. ID: " + id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{id}")
    public ResponseEntity<SimuladoComQuestoesResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(simuladoService.findById(id));
    }

    @GetMapping(value = "/api/simulados")
    public ResponseEntity<List<SimuladoDTO>> findAll() {
        return ResponseEntity.ok(simuladoService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<QuestaoSimuladoDTO>> findBySimuladoId(@PathVariable UUID simuladoId) {
        return ResponseEntity.ok(this.questaoSimuladoService.findBySimuladoId(simuladoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}", consumes = "application/json", 
        produces = "application/json")
    public ResponseEntity<QuestaoSimuladoDTO> findQuestaoById(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId) {
        return ResponseEntity.ok(this.questaoSimuladoService.findById(questaoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/simulados/{simuladoId}/questoes", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UUID> salvarQuestao(@PathVariable UUID simuladoId, @RequestBody QuestaoSimuladoDTO questao) {
        questao.setSimuladoId(simuladoId);
        UUID id = questaoSimuladoService.save(questao);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
