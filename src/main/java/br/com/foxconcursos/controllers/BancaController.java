package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Banca;
import br.com.foxconcursos.services.BancaService;
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
public class BancaController {

    private final BancaService bancaService;

    public BancaController(BancaService bancaService) {
        this.bancaService = bancaService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/bancas")
    public ResponseEntity<UUID> salvar(@RequestBody Banca banca) {
        banca = bancaService.salvar(banca);
        return ResponseEntity.status(HttpStatus.CREATED).body(banca.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/bancas/{id}")
    public ResponseEntity<Banca> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(bancaService.findById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/bancas/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Banca> atualizar(@PathVariable UUID id, @RequestBody Banca banca) {
        banca.setId(id);
        return ResponseEntity.ok(bancaService.salvar(banca));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') or hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping({
            "/api/admin/bancas",
            "/api/aluno/bancas"
    })
    public ResponseEntity<List<Banca>> listar(
            @RequestParam(required = false) String filter) throws Exception {
        return ResponseEntity.ok(bancaService.findAll(filter));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping(value = "/api/admin/bancas/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        bancaService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Banca deletada com sucesso.");
    }

}
