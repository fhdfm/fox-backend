package br.com.foxconcursos.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import br.com.foxconcursos.domain.Apostila;
import br.com.foxconcursos.dto.ApostilaRequest;
import br.com.foxconcursos.dto.ApostilaResponse;
import br.com.foxconcursos.repositories.ApostilaRepository;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ApostilaController {
    
    private final ApostilaRepository repository;

    public ApostilaController(ApostilaRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/apostilas")
    public ResponseEntity<UUID> create(@RequestBody ApostilaRequest request) {

        request.validateFields();

        Apostila apostila = request.toModel();

        this.repository.save(apostila);

        return ResponseEntity.status(HttpStatus.CREATED).body(apostila.getId());

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping("/api/admin/apostilas")
    public ResponseEntity<String> update(@PathVariable("id") UUID id, @RequestBody ApostilaRequest request) {

        request.validateFields();

        Optional<Apostila> apostila = this.repository.findById(id);

        if (!apostila.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Apostila com o ID: " + id + " não foi encontrado.");

        Apostila apostilaDB = apostila.get();
        apostilaDB.setDescricao(request.getDescricao());
        apostilaDB.setNome(request.getNome());
        apostilaDB.setImagem(request.getImagem());
        apostilaDB.setValor(request.getValor());
        apostilaDB.setValor(request.getValor());

        this.repository.save(apostilaDB);

        return ResponseEntity.status(HttpStatus.OK).body("Apostila com o ID: " + id + " foi atualizada com sucesso.");

    }

    @GetMapping({"/api/admin/apostilas/{id}", "/public/apostilas/{id}"})
    public ResponseEntity<Object> findById(@PathVariable UUID id) {
        
        Optional<Apostila> apostila = this.repository.findById(id);

        if (!apostila.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Apostila com o ID: " + id + " não foi encontrado.");

            return ResponseEntity.status(HttpStatus.OK).body(apostila.get().toAssembly());
    }

    @GetMapping({"/api/admin/apostilas", "/public/apostilas"})
    public ResponseEntity<List<ApostilaResponse>> listar() {
        
        List<ApostilaResponse> response = new ArrayList<>();

        List<Apostila> apostilas = this.repository.findAll();

        if (!apostilas.isEmpty()) {
            for (Apostila apostila : apostilas) {
                response.add(apostila.toAssembly());
            }
        }


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/apostilas/{id}")
    public ResponseEntity<String> deletar(@PathVariable("id") UUID id) {

        Optional<Apostila> apostila = this.repository.findById(id);

        if (!apostila.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Apostila com o ID: " + id + " não foi encontrado.");

        this.repository.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body("Apostila com o ID: " + id + " foi deletada com sucesso.");

    }

}
