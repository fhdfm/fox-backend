package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Instituicao;
import br.com.foxconcursos.services.InstituicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/instituicao")
    public ResponseEntity<UUID> salvar(@RequestBody Instituicao instituicao) {
        instituicao = instituicaoService.salvar(instituicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(instituicao.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') ")
    @PutMapping("/api/admin/instituicao/{id}")
    public ResponseEntity<Instituicao> atualizar(@PathVariable UUID id,
                                                 @RequestBody Instituicao instituicao) {
        instituicao.setId(id);
        return ResponseEntity.ok(instituicaoService.salvar(instituicao));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/instituicao/{id}")
    public ResponseEntity<Instituicao> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(instituicaoService.findById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping({
            "/api/admin/instituicao",
            "/api/aluno/instituicao"
    })
    public ResponseEntity<List<Instituicao>> listar(@RequestParam(required = false) String filter) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(instituicaoService.findAll(filter));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/instituicao/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        instituicaoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Instituicao deletada com sucesso: " + id);
    }
}