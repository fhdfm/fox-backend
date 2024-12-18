package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.services.CursoAlunoService;
import br.com.foxconcursos.services.StorageService;

@RestController
public class DownloadController {
    
    private StorageService storageService;

    private CursoAlunoService cursoAlunoService;

    public DownloadController(StorageService storageService, CursoAlunoService cursoAlunoService) {
        this.storageService = storageService;
        this.cursoAlunoService = cursoAlunoService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping("/api/aluno/download/{key}/curso/{cursoId}")
    public ResponseEntity<Void> downloadAluno(
            @PathVariable String key, @PathVariable UUID cursoId) throws IOException {
        
        this.cursoAlunoService.validarDownload(cursoId, key);

        String url = this.storageService.retrieveMedia(key);
            
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url)); // Define o redirecionamento para a URL do arquivo
        
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")   
    @GetMapping("/api/admin/download/{key}")
    public ResponseEntity<Void> downloadAdmin(@PathVariable String key) throws IOException {
        
        String url = this.storageService.retrieveMedia(key);
            
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url)); // Define o redirecionamento para a URL do arquivo
        
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

}
