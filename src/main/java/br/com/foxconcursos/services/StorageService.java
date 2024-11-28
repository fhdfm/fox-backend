package br.com.foxconcursos.services;

import java.io.IOException;
import java.security.GeneralSecurityException;

import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;

public interface StorageService {

    StorageOutput upload(StorageInput file) throws IOException, GeneralSecurityException;

    StorageOutput retrieveMedia(String fileId) throws IOException;
    
}
