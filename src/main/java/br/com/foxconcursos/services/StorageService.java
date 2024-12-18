package br.com.foxconcursos.services;

import java.io.IOException;

import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;

public interface StorageService {

    StorageOutput upload(StorageInput file) throws IOException;

    String retrieveMedia(String fileId) throws IOException;
    
}