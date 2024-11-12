package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.EscolaMilitar;
import br.com.foxconcursos.repositories.EscolaMilitarRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EscolaMilitarController {

    private final EscolaMilitarRepository repository;

    public EscolaMilitarController(EscolaMilitarRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/escola")
    public ResponseEntity<UUID> create(@RequestBody EscolaMilitar request) {
        this.repository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping("/api/admin/escola")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody EscolaMilitar request) {
        if (request.getNome() == null || request.getNome().trim().isEmpty())
            throw new IllegalArgumentException("O campo 'nome' é obrigatório e não está preenchido.");

        EscolaMilitar escola = this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Escola militar " + request.getNome() + " não foi encontrada."));

        escola.setNome(request.getNome());
        this.repository.save(escola);
        return ResponseEntity.status(HttpStatus.OK).body("Escola militar " + escola.getNome() + " foi atualizada com sucesso.");
    }

    @GetMapping({"/api/admin/escola/{id}", "/public/escola/{id}"})
    public ResponseEntity<Object> findById(@PathVariable UUID id) {
        EscolaMilitar escola = this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Escola com o ID: " + id + " não foi encontrada."));

        return ResponseEntity.ok(escola);
    }

    @GetMapping( "/public/escola")
    public ResponseEntity<List<EscolaMilitar>> listar(@RequestParam(required = false) String filter) throws Exception {

        if (filter == null || filter.isEmpty())
            return ResponseEntity.ok(this.repository.findAll());


        EscolaMilitar escolaFilter = FoxUtils.criarObjetoDinamico(filter, EscolaMilitar.class);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Iterable<EscolaMilitar> escolas =
                repository.findAll(
                        Example.of(escolaFilter, matcher));

        List<EscolaMilitar> response =
                StreamSupport.stream(escolas.spliterator(), false)
                        .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/escola/{id}")
    public ResponseEntity<String> deletar(@PathVariable("id") UUID id) {

        EscolaMilitar escola = this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Escola com o ID: " + id + " não foi encontrada."));

        this.repository.deleteById(escola.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Escola militar " + escola.getNome() + " foi deletada com sucesso.");
    }

}
