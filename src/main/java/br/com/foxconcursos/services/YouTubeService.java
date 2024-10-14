package br.com.foxconcursos.services;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import br.com.foxconcursos.domain.Storage;
import br.com.foxconcursos.domain.TipoArquivo;
import br.com.foxconcursos.dto.StorageRequest;
import br.com.foxconcursos.repositories.StorageRepository;
import br.com.foxconcursos.services.youtube.YouTubeAuth;

@Service
public class YouTubeService {
    
    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String URL = "https://www.youtube.com/watch?v=";

    private final StorageRepository repository;
    private final YouTubeAuth auth;

    public YouTubeService(StorageRepository repository, YouTubeAuth auth) {
        this.repository = repository;
        this.auth = auth;
    }

    @Transactional
    public UUID upload(StorageRequest request) throws IOException, GeneralSecurityException {

        Video metadata = buildVideoMetadata(request);

        File videoFile = convertMultipartFileToFile(request.getFile());

        try {

            Video uploadedVideo = uploadVideo(metadata, videoFile);

            Storage newVideoAula = saveStorage(uploadedVideo, request);

            return newVideoAula.getId();
        } finally {

            if (videoFile.exists())
                videoFile.delete();

        }

    }

    private Video buildVideoMetadata(StorageRequest request) {
        
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(Acessibilidade.UNLISTED.toString());

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle("TITULO");
        snippet.setDescription("DESCRICAO");

        Video metadata = new Video();
        metadata.setSnippet(snippet);
        metadata.setStatus(status);

        return metadata;
    }

    private Video uploadVideo(Video metadata, File videoFile) throws IOException, GeneralSecurityException {

        FileContent content = new FileContent("video/*", videoFile);

        Credential credential = this.auth.getAccessToken();

        YouTube youTube = new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,
            credential)
            .setApplicationName(APPLICATION_NAME)
            .build();

        YouTube.Videos.Insert videoUploader = youTube.videos()
                .insert("snippet,statistics,status", metadata, content);

        try {
            return videoUploader.execute();
        } catch (GoogleJsonResponseException e) {
            // logar
            throw new RuntimeException("Erro ao fazer upload de vídeo para o YouTube.", e);
        }

    }

    private Storage saveStorage(Video uploadVideo, StorageRequest request) {
        
        String videoUrl = URL + uploadVideo.getId();

        ThumbnailDetails thumbnailDetails = uploadVideo.getSnippet().getThumbnails();
        String thumbnailUrl = thumbnailDetails.getDefault().getUrl();

        Storage newAula = new Storage();
        newAula.setThumbnail(thumbnailUrl);
        newAula.setUrl(videoUrl);
        newAula.setTipo(TipoArquivo.VIDEO);
        newAula.setAssuntoId(request.getAssuntoId());
        newAula.setDisciplinaId(request.getDisciplinaId());

        return repository.save(newAula);
    }
    
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File newFile = new File(
                System.getProperty("java.io.tmpdir") 
                    + File.separator 
                    + file.getOriginalFilename());
        file.transferTo(newFile);
        newFile.deleteOnExit();
        return newFile;
    }

    private enum Acessibilidade {
        PRIVATE, UNLISTED
    }
}
