package br.com.foxconcursos.services.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.dto.GoogleDriveResponse;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.dto.YouTubeResponse;
import br.com.foxconcursos.services.GoogleDriveService;
import br.com.foxconcursos.services.StorageService;
import br.com.foxconcursos.services.YouTubeService;

@Service
public class StorageServiceImpl implements StorageService {

    private final GoogleDriveService googleDriveService;
    private final YouTubeService youTubeService;

    public StorageServiceImpl(GoogleDriveService googleDriveService, YouTubeService youTubeService) {
        this.googleDriveService = googleDriveService;
        this.youTubeService = youTubeService;
    }

    @Override
    public StorageOutput upload(StorageInput input) throws IOException, GeneralSecurityException {
        
        if (input.isMovie()) {
            YouTubeResponse response = youTubeService.upload(input);
            return response.get();
        }

        GoogleDriveResponse response = googleDriveService.upload(input);
        return response.get();
    }

    @Override
    public StorageOutput retrieveMedia(String fileId) throws IOException {
        GoogleDriveResponse response = this.googleDriveService.retrieveMedia(fileId);
        return response.get();
    }    
}
