package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.TipoArquivo;
import br.com.foxconcursos.dto.AulaConteudoRequest;
import br.com.foxconcursos.dto.AulaRequest;
import br.com.foxconcursos.dto.AulaResponse;
import br.com.foxconcursos.services.AulaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

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
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> cadastrarConteudo(
            UUID aulaId, @RequestParam("fileName") String fileName, 
            @RequestParam("tipo") String tipo, @RequestParam("titulo") String titulo, 
            HttpServletRequest request) throws IOException, GeneralSecurityException {

        try (InputStream inputStream = request.getInputStream()) {
        
                AulaConteudoRequest aulaConteudoRequest = new AulaConteudoRequest();
                aulaConteudoRequest.setFile(inputStream);
                aulaConteudoRequest.setTitulo(titulo);
                aulaConteudoRequest.setTipo(TipoArquivo.fromString(tipo));
                aulaConteudoRequest.setFileName(fileName);

                                        
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(this.service.criarConteudo(aulaId, aulaConteudoRequest));

        } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/aula/{aulaId}/conteudo/{conteudoId}",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> atualizarConteudo(
            UUID aulaId, UUID conteudoId, @RequestParam("fileName") String fileName, 
            @RequestParam("tipo") String tipo, @RequestParam("titulo") String titulo, 
            HttpServletRequest request) throws IOException, GeneralSecurityException {
        
        try (InputStream inputStream = request.getInputStream()) {
                
                AulaConteudoRequest aulaConteudoRequest = new AulaConteudoRequest();
                aulaConteudoRequest.setFile(inputStream);
                aulaConteudoRequest.setTitulo(titulo);
                aulaConteudoRequest.setTipo(TipoArquivo.fromString(tipo));
                aulaConteudoRequest.setFileName(fileName);

                this.service.atualizarConteudo(aulaId, conteudoId, aulaConteudoRequest);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Conteudo atualizado com sucesso.");

        } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/aula", 
        produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AulaResponse>> list(
                @RequestParam(required = false, value = "titulo", defaultValue = "") String titulo, 
                @RequestParam(required = false, value = "cursoId", defaultValue = "00000000-0000-0000-0000-000000000000") UUID cursoId, 
                @RequestParam(required = false, value = "disciplinaId", defaultValue = "00000000-0000-0000-0000-000000000000") UUID disciplinaId, 
                @RequestParam(required = false, value = "assuntoId", defaultValue = "00000000-0000-0000-0000-000000000000") UUID assuntoId) {

        return ResponseEntity.status(HttpStatus.OK).body(
                        this.service.buscarPorParametros(titulo, cursoId, disciplinaId, assuntoId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/api/admin/aula/{id}", 
        produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AulaResponse> findById(@PathVariable("id") UUID id) {

        return ResponseEntity.status(HttpStatus.OK).body(
                        this.service.buscarPorId(id));
    }    

}
