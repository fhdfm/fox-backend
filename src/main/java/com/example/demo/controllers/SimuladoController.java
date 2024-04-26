package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.QuestaoSimuladoRequest;
import com.example.demo.dto.QuestaoSimuladoResponse;
import com.example.demo.dto.QuestoesSimuladoDisciplinaResponse;
import com.example.demo.dto.SimuladoCompletoResponse;
import com.example.demo.dto.SimuladoRequest;
import com.example.demo.dto.SimuladoResponse;
import com.example.demo.dto.SimuladoResumoResponse;
import com.example.demo.services.AuthenticationService;
import com.example.demo.services.QuestaoSimuladoService;
import com.example.demo.services.RespostaSimuladoService;
import com.example.demo.services.SimuladoService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SimuladoController {
    
    private final SimuladoService simuladoService;
    private final QuestaoSimuladoService questaoSimuladoService;
    private final AuthenticationService authenticationService;
    private final RespostaSimuladoService respostaSimuladoService;

    public SimuladoController(SimuladoService simuladoService, 
        QuestaoSimuladoService questaoSimuladoService, 
        AuthenticationService authenticationService, 
        RespostaSimuladoService respostaSimuladoService) {
        
        this.simuladoService = simuladoService;
        this.questaoSimuladoService = questaoSimuladoService;
        this.authenticationService = authenticationService;
        this.respostaSimuladoService = respostaSimuladoService;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/simulados", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> save(@RequestBody SimuladoRequest request) {
        UUID id = simuladoService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping(value = "/api/simulados")
    public ResponseEntity<List<SimuladoResumoResponse>> findAll(
        @RequestParam(required = false) String filter) throws Exception {
        if (filter != null) {
            return ResponseEntity.ok(simuladoService.findByExample(filter));
        }        
        return ResponseEntity.ok(simuladoService.findAll());
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
    public ResponseEntity<SimuladoCompletoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(simuladoService.findById(id));
    }

    /* Questões do Simulado */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes")
    public ResponseEntity<QuestoesSimuladoDisciplinaResponse>
        findBySimuladoId(@PathVariable UUID simuladoId) {
        UUID cursoId = simuladoService.getCursoAssociado(simuladoId);
        return ResponseEntity.ok(
            this.questaoSimuladoService.findBySimuladoId(cursoId, simuladoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}")
    public ResponseEntity<QuestaoSimuladoResponse> findQuestaoById(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId) {
        return ResponseEntity.ok(this.questaoSimuladoService.findById(questaoId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/simulados/{simuladoId}/questoes", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvarQuestao(@PathVariable UUID simuladoId, 
        @RequestBody QuestaoSimuladoRequest request) {
        UUID id = questaoSimuladoService.save(simuladoId, request);
        simuladoService.incrementarQuestoes(simuladoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/api/admin/simulados/{simuladoId}/questoes/{questaoId}", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestaoSimuladoResponse> atualizarQuestao(
        @PathVariable UUID simuladoId, @PathVariable UUID questaoId,
        @RequestBody QuestaoSimuladoRequest request) {
        
        QuestaoSimuladoResponse response = questaoSimuladoService.save(
            simuladoId, questaoId, request);
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ALUNO') or hasRole('EXTERNO')")
    @PostMapping(value = "/api/alunos/simulados/{simuladoId}/iniciar", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimuladoCompletoResponse> iniciarSimulado(
        @PathVariable UUID simuladoId) {
        
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        respostaSimuladoService.iniciar(simuladoId, authentication.getName());

        return ResponseEntity.ok(simuladoService.findById(simuladoId, false));
    }
}
