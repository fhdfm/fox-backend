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

import br.com.foxconcursos.domain.EscolaMilitar;
import br.com.foxconcursos.dto.EscolaMilitarRequest;
import br.com.foxconcursos.dto.EscolaMilitarResponse;
import br.com.foxconcursos.repositories.EscolaMilitarRepository;
import br.com.foxconcursos.util.FoxUtils;

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
    public ResponseEntity<UUID> create(@RequestBody EscolaMilitarRequest request) {

        request.validateFields();

        EscolaMilitar escola = request.toModel();

        this.repository.save(escola);

        return ResponseEntity.status(HttpStatus.CREATED).body(escola.getId());

    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping("/api/admin/escola")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody EscolaMilitarRequest request) {

        request.validateFields();

        EscolaMilitar escola = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                       "Escola com o ID: " + id + " não foi encontrada."));

       escola.updateFromRequest(request);

        this.repository.save(escola);

        return ResponseEntity.status(HttpStatus.OK).body("Escola com o ID: " + id + " foi atualizada com sucesso.");

    }

    @GetMapping({"/api/admin/escola/{id}", "/public/escola/{id}"})
    public ResponseEntity<Object> findById(@PathVariable UUID id) {
        
        EscolaMilitar escola = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                   "Escola com o ID: " + id + " não foi encontrada."));
        
        return ResponseEntity.status(HttpStatus.OK).body(escola.toAssembly());
    }

    @GetMapping({"/api/admin/escola", "/public/escola"})
    public ResponseEntity<List<EscolaMilitarResponse>> listar(@RequestParam(required = false) String filter) throws Exception {
        
        if (filter == null || filter.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(this.repository.findAll()
                    .stream()
                    .map(EscolaMilitar::toAssembly)
                    .collect(Collectors.toList()));
        
        EscolaMilitar escolaFilter = FoxUtils.criarObjetoDinamico(filter, EscolaMilitar.class);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos
            
        Example<EscolaMilitar> example = Example.of(escolaFilter, matcher);

        Iterable<EscolaMilitar> escolaIterable = this.repository.findAll(example);
        
        List<EscolaMilitarResponse> escolas = StreamSupport.stream(escolaIterable.spliterator(), false)
                .map(EscolaMilitar::toAssembly)
                .collect(Collectors.toList());
        
        
        return ResponseEntity.status(HttpStatus.OK).body(escolas);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/escola/{id}")
    public ResponseEntity<String> deletar(@PathVariable("id") UUID id) {

        EscolaMilitar escola = this.repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                   "Escola com o ID: " + id + " não foi encontrada."));

        this.repository.delete(escola);

        return ResponseEntity.status(HttpStatus.OK).body("Escola com o ID: " + id + " foi deletada com sucesso.");

    }

}
