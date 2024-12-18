package br.com.foxconcursos.services.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.services.S3Service;
import br.com.foxconcursos.services.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

    private final S3Service service;

    public StorageServiceImpl(S3Service s3Service) {
        this.service = s3Service;
    }

    @Override
    public StorageOutput upload(StorageInput file) throws IOException {
        S3Response response = this.service.upload(file);
        return response.get();
    }

    @Override
    public String retrieveMedia(String fileId) throws IOException {
        S3Response response = this.service.getFile(fileId);
        return response.getUrl();
    } 
}
