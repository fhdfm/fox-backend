package br.com.foxconcursos.services;

import java.io.IOException;

import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.services.S3Service.S3File;

public interface StorageService {

    StorageOutput upload(StorageInput file) throws Exception;

    String getLink(String fileId) throws IOException;
    
    public void delete(String key);

    public S3File retrieveMedia(String key);
}