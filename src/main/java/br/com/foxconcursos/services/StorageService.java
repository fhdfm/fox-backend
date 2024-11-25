package br.com.foxconcursos.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import br.com.foxconcursos.dto.StorageRequest;

public interface StorageService {

    UUID upload(StorageRequest storageRequest) throws IOException, GeneralSecurityException;

    String upload(MultipartFile file) throws IOException, GeneralSecurityException;

    List<File> list(String content, String folderId) throws IOException;

    void deleteEmptyFolder(String folderId) throws IOException;

    String createFolder(String folderName, String parentFolder) throws IOException;

    InputStreamResource retrieveMedia(String fileId) throws IOException;

    File getFile(String fileId) throws IOException;
    
}
