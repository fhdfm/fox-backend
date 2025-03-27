package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.services.CursoAlunoService;
import br.com.foxconcursos.services.S3Service;
import br.com.foxconcursos.services.StorageService;

@RestController
public class DownloadController {
    
    private StorageService storageService;

    private CursoAlunoService cursoAlunoService;

    public DownloadController(StorageService storageService, CursoAlunoService cursoAlunoService) {
        this.storageService = storageService;
        this.cursoAlunoService = cursoAlunoService;
    }

    @GetMapping("/api/download/curso/{cursoId}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') or hasAuthority('SCOPE_ROLE_ALUNO')")
    public ResponseEntity<String> download(@RequestParam String key, @PathVariable UUID cursoId) throws IOException {
        
        if (Objects.equals(key, "null"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arquivo não encontrado.");

        //this.cursoAlunoService.validarDownload(cursoId, key);

        String link = this.storageService.getLink(key);

        return ResponseEntity.ok(link);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/api/aluno/download/{key}/curso/{cursoId}")
    public ResponseEntity<byte[]> downloadAluno(
            @PathVariable String key, @PathVariable UUID cursoId) throws IOException {
        
        //this.cursoAlunoService.validarDownload(cursoId, key);

        try {
            S3Service.S3File file = this.storageService.retrieveMedia(key);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key);
            headers.add(HttpHeaders.CONTENT_TYPE, file.getContentType());
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.getContent().length)
                .body(file.getContent());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")   
    @GetMapping("/api/admin/download/{key}")
    public ResponseEntity<byte[]> downloadAdmin(@PathVariable String key) throws IOException {
        
        try {
            S3Service.S3File file = this.storageService.retrieveMedia(key);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key);
            headers.add(HttpHeaders.CONTENT_TYPE, file.getContentType());
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.getContent().length)
                .body(file.getContent());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
