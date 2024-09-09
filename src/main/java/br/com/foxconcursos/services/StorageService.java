package br.com.foxconcursos.services;

import java.io.IOException;
import java.util.List;

import com.google.api.services.drive.model.File;

import br.com.foxconcursos.dto.StorageRequest;

public interface StorageService {

    String uploadFile(StorageRequest storageRequest) throws IOException;

    List<File> list(String content, String folderId) throws IOException;

    void deleteEmptyFolder(String folderId) throws IOException;

    String createFolder(String folderName, String parentFolder) throws IOException;
    
}
