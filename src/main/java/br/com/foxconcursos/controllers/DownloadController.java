package br.com.foxconcursos.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.dto.StorageOutput;
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
    @GetMapping("/api/aluno/download/{fileId}/curso/{cursoId}")
    public ResponseEntity<InputStreamResource> downloadAluno(
            @PathVariable String fileId, @PathVariable UUID cursoId) throws IOException {
        this.cursoAlunoService.validarDownload(cursoId, fileId);
        StorageOutput file = this.storageService.retrieveMedia(fileId);
        return getResponse(file);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")   
    @GetMapping("/api/admin/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadAdmin(@PathVariable String fileId) throws IOException {
        StorageOutput file = this.storageService.retrieveMedia(fileId);
        return getResponse(file);

    }

    private ResponseEntity<InputStreamResource> getResponse(StorageOutput output) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + output.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, output.getMimeType());
        return ResponseEntity.ok().headers(headers).body(output.getFileStream());   
    }

}
