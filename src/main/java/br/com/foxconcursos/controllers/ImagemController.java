package br.com.foxconcursos.controllers;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.services.StorageService;

@RestController
public class ImagemController {
    
    private StorageService storageService;

    public ImagemController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/image/{fileId}")
    public ResponseEntity<InputStreamResource> loadImage(@PathVariable String fileId) throws IOException {

        InputStreamResource media = this.storageService.retrieveMedia(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok().headers(headers).body(media);

    }

}
