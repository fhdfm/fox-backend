package br.com.foxconcursos.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Instituicao;
import br.com.foxconcursos.services.InstituicaoService;


@RestController
@RequestMapping(value = "/api/admin/instituicao", produces = MediaType.APPLICATION_JSON_VALUE)
public class InstituicaoController {
 
    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@RequestBody Instituicao instituicao) {
        instituicao = instituicaoService.salvar(instituicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(instituicao.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Instituicao> atualizar(@PathVariable UUID id,
        @RequestBody Instituicao instituicao) {
        instituicao.setId(id);
        return ResponseEntity.ok(instituicaoService.salvar(instituicao));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Instituicao> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(instituicaoService.findById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Instituicao>> listar(
        @RequestParam(required = false) String filter) throws Exception {
        
        return ResponseEntity.ok(instituicaoService.findAll(filter));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        instituicaoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Instituicao deletada com sucesso: " + id);
    }
}
