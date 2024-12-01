package br.com.foxconcursos.controllers;

import br.com.foxconcursos.dto.AulaConteudoRequest;
import br.com.foxconcursos.dto.AulaRequest;
import br.com.foxconcursos.services.AulaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@RestController
public class AulaController {

    private AulaService service;

    public AulaController(AulaService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(path = "/api/admin/aula",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> cadastrarAula(@RequestBody AulaRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.service.criarAula(request));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(path = "/api/admin/aula/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> atualizarAula(@PathVariable UUID id, @RequestBody AulaRequest request) {
        this.service.atualizarAula(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Aula atualizada com sucesso.");
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/aula/{aulaId}/conteudo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> cadastrarConteudo(
            UUID aulaId, @ModelAttribute AulaConteudoRequest request) throws IOException, GeneralSecurityException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.service.criarConteudo(aulaId, request));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/aula/{aulaId}/conteudo/{conteudoId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> atualizarConteudo(
            UUID aulaId, UUID conteudoId, @ModelAttribute AulaConteudoRequest request) throws IOException, GeneralSecurityException {

        this.service.atualizarConteudo(aulaId, conteudoId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Conteudo atualizado com sucesso.");
    }

}
