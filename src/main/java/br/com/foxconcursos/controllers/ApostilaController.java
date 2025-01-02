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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.foxconcursos.domain.Apostila;
import br.com.foxconcursos.dto.ApostilaRequest;
import br.com.foxconcursos.dto.ApostilaResponse;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.ApostilaRepository;
import br.com.foxconcursos.services.StorageService;
import br.com.foxconcursos.util.FoxUtils;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ApostilaController {

    private final ApostilaRepository repository;

    private final StorageService storageService;

    public ApostilaController(ApostilaRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping(value = "/api/admin/apostilas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> create(@ModelAttribute ApostilaRequest request) throws Exception {

        request.validate();

        Apostila apostila = request.toModel();

        MultipartFile file = request.getImagem();

        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .isPublic(true)
                .withFileName("imagens/" +file.getOriginalFilename())
                .withMimeType(file.getContentType())
                .withFileSize(file.getSize())
                .build();

        if (!input.isImage()) 
            throw new IllegalArgumentException("Tipo de media não permitida.");
        
        StorageOutput output = storageService.upload(input);
        apostila.setImagem(output.getUrl());

        this.repository.save(apostila);

        return ResponseEntity.status(HttpStatus.CREATED).body(apostila.getId());

    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PutMapping(value = "/api/admin/apostilas/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> update(@PathVariable UUID id, @ModelAttribute ApostilaRequest request) throws Exception {

        Apostila apostila = this.repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Apostila com o ID: " + id + " não foi encontrada."));
        
        request.validate(false);

        if (request.hasUpload()) {

            MultipartFile file = request.getImagem();

            StorageInput input = new StorageInput.Builder()
                    .withFileInputStream(file.getInputStream())
                    .isPublic(true)
                    .withFileName("imagens/" + file.getOriginalFilename())
                    .withMimeType(file.getContentType())
                    .withFileSize(file.getSize())
                    .build();

            if (!input.isImage()) 
                throw new IllegalArgumentException("Tipo de media não permitida.");

            StorageOutput output = storageService.upload(input);
            apostila.setImagem(output.getUrl());
        }

        apostila.updateFromRequest(id, request);

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
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

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

        this.repository.deleteById(apostila.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Apostila deletada com sucesso.");
    }

}
