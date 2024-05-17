package br.com.foxconcursos.services;

import org.springframework.stereotype.Service;

@Service
public class StorageService {
    
    // TODO - Implements integration with the amazon s3 service
    public String upload(byte[] imageBytes) {
        return "https://www.example.com/image.jpg";
    }

}