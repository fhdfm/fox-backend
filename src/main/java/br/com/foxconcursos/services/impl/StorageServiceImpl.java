package br.com.foxconcursos.services.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.S3Response;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.services.S3Service;
import br.com.foxconcursos.services.S3Service.S3File;
import br.com.foxconcursos.services.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

    private final S3Service service;

    public StorageServiceImpl(S3Service s3Service) {
        this.service = s3Service;
    }

    @Override
    public StorageOutput upload(StorageInput object) throws Exception {
        
        if ("videos/".startsWith(object.getPrefix()))
            throw new IllegalArgumentException("Não é permitido upload de vídeos");

        S3Response response = this.service.upload(object);

        return response.get();
    }

    @Override
    public String getLink(String key) throws IOException {
        return this.service.getLink(key);
    }

    @Override
    public void delete(String key) {
        this.service.delete(key);
    }

    @Override
    public S3File retrieveMedia(String key) {
        return this.service.getMedia(key);
    }

}