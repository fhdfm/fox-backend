package br.com.foxconcursos.services.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import br.com.foxconcursos.dto.StorageRequest;
import br.com.foxconcursos.services.GoogleDriveService;
import br.com.foxconcursos.services.StorageService;
import br.com.foxconcursos.services.YouTubeService;
import br.com.foxconcursos.util.FileTypeChecker;

@Service
public class StorageServiceImpl implements StorageService {

    private final GoogleDriveService googleDriveService;
    private final YouTubeService youTubeService;

    public StorageServiceImpl(GoogleDriveService googleDriveService, YouTubeService youTubeService) {
        this.googleDriveService = googleDriveService;
        this.youTubeService = youTubeService;
    }

    @Override
    public UUID upload(StorageRequest storageRequest) throws IOException, GeneralSecurityException {
        
        MultipartFile file = storageRequest.getFile();
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Arquivo inválido.");

        if (FileTypeChecker.isMovie(file))
            return youTubeService.upload(storageRequest);

        return googleDriveService.upload(storageRequest);
    }

    @Override
    public String upload(MultipartFile file) throws IOException, GeneralSecurityException {
        
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Arquivo inválido.");

        // if (FileTypeChecker.isMovie(file))
        //     return youTubeService.upload(file);

        return googleDriveService.upload(file);
    }

    @Override
    public List<File> list(String content, String folderId) throws IOException {
        return googleDriveService.list("FOLDER", folderId);
    }

    @Override
    public void deleteEmptyFolder(String folderId) throws IOException {
        googleDriveService.deleteEmptyFolder(folderId);
    }

    @Override
    public String createFolder(String folderName, String parentFolder) throws IOException {
        return googleDriveService.createFolder(folderName, parentFolder);
    }

    @Override
    public InputStreamResource retrieveMedia(String fileId) throws IOException {
        return this.googleDriveService.retrieveMedia(fileId);
    }

    @Override
    public File getFile(String fileId) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFile'");
    }
    
}
