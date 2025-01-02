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
    public StorageOutput upload(StorageInput object) throws Exception {
        
        S3Response response;
        if (object.isFileLargerThan5MB())
            response = this.service.uploadLargeFiles(object);
        else
            response = this.service.upload(object);

        return response.get();
    }

    @Override
    public String retrieveMedia(String key) throws IOException {
        return this.service.getFile(key);
    }
}