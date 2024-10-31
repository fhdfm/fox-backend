package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
import org.springframework.web.server.ResponseStatusException;

import br.com.foxconcursos.domain.Apostila;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.dto.ApostilaRequest;
import br.com.foxconcursos.dto.ApostilaResponse;
import br.com.foxconcursos.repositories.ApostilaRepository;
import br.com.foxconcursos.util.FoxUtils;

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
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody ApostilaRequest request) {

        request.validateFields();

        Apostila apostila = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                       "Apostila com o ID: " + id + " não foi encontrada."));

       apostila.updateFromRequest(request);

        this.repository.save(apostila);

        return ResponseEntity.status(HttpStatus.OK).body("Apostila com o ID: " + id + " foi atualizada com sucesso.");

    }

    @GetMapping({"/api/admin/apostilas/{id}", "/public/apostilas/{id}"})
    public ResponseEntity<Object> findById(@PathVariable UUID id) {
        
        Apostila apostila = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                   "Apostila com o ID: " + id + " não foi encontrada."));
        
        return ResponseEntity.status(HttpStatus.OK).body(apostila.toAssembly());
    }

    @GetMapping({"/api/admin/apostilas", "/public/apostilas"})
    public ResponseEntity<List<ApostilaResponse>> listar(@RequestParam(required = false) String filter) throws Exception {
        
        if (filter == null || filter.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(this.repository.findAll()
                    .stream()
                    .map(Apostila::toAssembly)
                    .collect(Collectors.toList()));
        
        Apostila apostilaFilter = FoxUtils.criarObjetoDinamico(filter, Apostila.class);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos
            
        Example<Apostila> example = Example.of(apostilaFilter, matcher);

        Iterable<Apostila> apostilaIterable = this.repository.findAll(example);
        
        List<ApostilaResponse> apostilas = StreamSupport.stream(apostilaIterable.spliterator(), false)
                .map(Apostila::toAssembly)
                .collect(Collectors.toList());
        
        
        return ResponseEntity.status(HttpStatus.OK).body(apostilas);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/apostilas/{id}")
    public ResponseEntity<String> deletar(@PathVariable("id") UUID id) {

        Apostila apostila = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                   "Apostila com o ID: " + id + " não foi encontrada."));

        apostila.setStatus(Status.INATIVO);                                                   
        this.repository.save(apostila);

        return ResponseEntity.status(HttpStatus.OK).body("Apostila com o ID: " + id + " foi deletada com sucesso.");

    }

}
