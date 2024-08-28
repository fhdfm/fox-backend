package br.com.foxconcursos.controllers;

import br.com.foxconcursos.services.GoogleDriveService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class StorageController {

    private final GoogleDriveService googleDriveService;

    // pasta raiz, depois injetar
    private final String ROOT = "1d79_3bhvJcfFl32S3PJQ-gKjHftfpyG4";

    public StorageController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/api/admin/storage/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("folderId") String folderId) throws Exception {

        File convFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        String fileId = this.googleDriveService.uploadFile(
                convFile, file.getContentType(), folderId);

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
        if ("FILES".equalsIgnoreCase(content))
            return ResponseEntity.ok(this.googleDriveService.listFilesInFolder(folderId));

        if ("FOLDERS".equalsIgnoreCase(content))
            ResponseEntity.ok(this.googleDriveService.listFoldersInFolder(folderId));

        return ResponseEntity.ok(this.googleDriveService.listAllInFolder(folderId));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/api/admin/storage/delete/{folderId}")
    public ResponseEntity<String> deletarFolder(@PathParam("folderId") String folderId) {

        if (ROOT.equals(folderId))
            throw new IllegalArgumentException("Não é possível apagar a pasta raiz.");

        try {
            this.googleDriveService.deleteEmptyFolder(folderId);
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
            String id = this.googleDriveService.createFolder(folderName, parentFolder);
            return ResponseEntity.ok("Pasta: " + folderName + " (" + id + ") criada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.ok("Erro ao criar pasta: " + folderName + " | Exc: " + e.getMessage());
        }
    }
}
