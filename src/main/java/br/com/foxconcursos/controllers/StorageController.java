package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.dto.StorageRequest;
import br.com.foxconcursos.services.StorageService;
import jakarta.websocket.server.PathParam;

@RestController
public class StorageController {

    private final StorageService service;

    // pasta raiz, depois injetar
    private final String ROOT = "1d79_3bhvJcfFl32S3PJQ-gKjHftfpyG4";

    public StorageController(StorageService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/storage/upload")
    public ResponseEntity<UUID> upload(@ModelAttribute StorageRequest storageRequest) throws Exception {
        UUID fileId = this.service.upload(storageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(fileId);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/api/admin/storage/list")
    public ResponseEntity<List<com.google.api.services.drive.model.File>> listar(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "folderId", required = false) String folderId) throws IOException {

        if (folderId == null) {
            folderId = this.ROOT;
        }

        return ResponseEntity.ok(this.service.list(content, folderId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/storage/delete/{folderId}")
    public ResponseEntity<String> deletarFolder(@PathParam("folderId") String folderId) {

        if (ROOT.equals(folderId))
            throw new IllegalArgumentException("Não é possível apagar a pasta raiz.");

        try {
            this.service.deleteEmptyFolder(folderId);
            return ResponseEntity.ok("Pasta: " + folderId + " deletada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.ok("Erro ao deletar pasta: " + folderId + " | Exc: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/storage/create-folder")
    public ResponseEntity<String> createFolder(@RequestParam(value = "folderName", required = true) String folderName,
                                               @RequestParam(value = "parentFolder", required = false) String parentFolder) {

        if (folderName == null)
            throw new IllegalArgumentException("É necessário informar o nome da pasta.");

        if (parentFolder == null)
            parentFolder = this.ROOT;

        try {
            String id = this.service.createFolder(folderName, parentFolder);
            return ResponseEntity.ok("Pasta: " + folderName + " (" + id + ") criada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.ok("Erro ao criar pasta: " + folderName + " | Exc: " + e.getMessage());
        }
    }

}
