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

import br.com.foxconcursos.domain.Carrossel;
import br.com.foxconcursos.dto.CarrosselRequest;
import br.com.foxconcursos.services.CarrosselService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CarrosselController {
    
    private final CarrosselService carrosselService;

    public CarrosselController(CarrosselService carrosselService) {
        this.carrosselService = carrosselService;
    }

    @GetMapping(value = {"/public/carrossel", "/api/admin/carrossel"})
    public ResponseEntity<List<Carrossel>> findAll() {
        List<Carrossel> carrossels = carrosselService.findAll();
        return ResponseEntity.ok(carrossels);
    }

    @GetMapping(value = "/api/admin/carrossel/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Carrossel> findById(@PathVariable UUID id) {
        Carrossel carrossel = carrosselService.findById(id);
        return ResponseEntity.ok(carrossel);
    }

    @DeleteMapping(value = "/api/admin/carrossel/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        carrosselService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/admin/carrossel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<UUID> save(@ModelAttribute CarrosselRequest request) throws Exception {
        Carrossel carrossel = carrosselService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrossel.getId());
    }

    @PutMapping(value = "/api/admin/carrossel/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable UUID id, @ModelAttribute CarrosselRequest request) throws Exception {
        carrosselService.update(id, request);
        return ResponseEntity.ok().build();
    }
}
