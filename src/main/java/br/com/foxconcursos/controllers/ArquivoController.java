package br.com.foxconcursos.controllers;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.services.GoogleDriveService;

@RestController
public class ArquivoController {
    
    private final GoogleDriveService googleDriveService;

    public ArquivoController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @PostMapping("/upload-video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file, 
        @RequestParam("folderId") String foldeId) throws Exception {
        
        File convFile = new File(System.getProperty("java.io.tmpdir") 
            + File.separator + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());    
        fos.close();

        String fileId = this.googleDriveService.uploadFile(
            convFile, file.getContentType(), foldeId);
        
        return ResponseEntity.status(HttpStatus.OK).body(fileId);
    }

}
