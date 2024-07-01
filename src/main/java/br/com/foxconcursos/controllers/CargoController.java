package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Cargo;
import br.com.foxconcursos.services.CargoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/admin/cargo", produces = MediaType.APPLICATION_JSON_VALUE)
public class CargoController {
 
    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> salvar(@RequestBody Cargo cargo) {
        cargo = cargoService.salvar(cargo);
        return ResponseEntity.status(HttpStatus.CREATED).body(cargo.getId());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cargo> atualizar(@PathVariable UUID id,
        @RequestBody Cargo cargo) {
        cargo.setId(id);
        return ResponseEntity.ok(cargoService.salvar(cargo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Cargo> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(cargoService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Cargo>> listar(
        @RequestParam(required = false) String filter) throws Exception {
        
        return ResponseEntity.ok(cargoService.findAll(filter));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletar(@PathVariable UUID id) {
        cargoService.deletar(id);
        return ResponseEntity.status(HttpStatus.OK).body("Cargo deletado com sucesso: " + id);
    }
}
