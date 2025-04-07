package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Edital;
import br.com.foxconcursos.dto.EditalRequest;
import br.com.foxconcursos.services.EditalService;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EditalController {
    
    private final EditalService editalService;

    public EditalController(EditalService editalService) {
        this.editalService = editalService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/edital", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> create(@ModelAttribute EditalRequest request) throws Exception {
        UUID id = this.editalService.create(request);
        return ResponseEntity.ok(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/edital/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> update(@ModelAttribute EditalRequest request, @PathVariable("id") UUID id) throws Exception {
        this.editalService.update(request, id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/edital/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        this.editalService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping({"/api/admin/edital", "/public/edital"})
    public ResponseEntity<List<Edital>> findAll() {
        List<Edital> editais = this.editalService.findAll();
        return ResponseEntity.ok(editais);
    }

    @GetMapping({"/api/admin/edital/{id}", "/public/edital/{id}"})
    public ResponseEntity<Edital> findbyId(@PathVariable("id") UUID id) {
        Edital edital = this.editalService.findById(id);
        return ResponseEntity.ok(edital);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/edital/activate/{id}")
    public ResponseEntity<Void> activate(@PathVariable("id") UUID id) {
        this.editalService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/edital/deactivate/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable("id") UUID id) {
        this.editalService.deactivate(id);
        return ResponseEntity.ok().build();
    }    
}
