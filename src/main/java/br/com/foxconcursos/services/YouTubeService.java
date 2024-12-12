package br.com.foxconcursos.services;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.YouTubeResponse;
import br.com.foxconcursos.services.youtube.YouTubeAuth;

@Service
public class YouTubeService {
    
    private static final String APPLICATION_NAME = "fox-backend";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String URL = "https://www.youtube.com/watch?v=";

    private final YouTubeAuth auth;

    public YouTubeService(YouTubeAuth auth) {
        this.auth = auth;
    }

    public YouTubeResponse upload(StorageInput input) throws IOException, GeneralSecurityException {

        Video metadata = buildVideoMetadata();

        File videoFile = convertMultipartFileToFile(input.getInputStream());

        try {

            Video uploadedVideo = uploadVideo(metadata, videoFile);

            String videoUrl = URL + uploadedVideo.getId();

            ThumbnailDetails thumbnailDetails = uploadedVideo.getSnippet().getThumbnails();
            String thumbnailUrl = thumbnailDetails.getDefault().getUrl();

            return new YouTubeResponse(videoUrl, thumbnailUrl);
        } finally {

            if (videoFile.exists())
                videoFile.delete();

        }

    }

    private Video buildVideoMetadata() {
        
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
            .setHttpRequestInitializer(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setConnectTimeout(120000); // 120 segundos
                    request.setReadTimeout(120000); // 120 segundos
                }
            })
            .build();

        YouTube.Videos.Insert videoUploader = youTube.videos()
                .insert("snippet,statistics,status", metadata, content);

        MediaHttpUploader uploader = videoUploader.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(false);

        uploader.setProgressListener(progress -> {
            System.out.println("Progress: " + progress.getProgress());
        });

        int MAX_RETRIES = 3;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return videoUploader.execute();
            } catch (SocketTimeoutException | GoogleJsonResponseException e) {
                if (attempt == MAX_RETRIES) throw e; // Re-throw após tentativas
                System.err.println("Erro no upload (tentativa " + attempt + "): " + e.getMessage());
                try {
                    Thread.sleep(2000 * attempt); // Atraso exponencial
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restaura o status de interrupção da thread
                    throw new RuntimeException("Thread interrompida durante o retry do upload.", ie);
                }
            }
        }

        throw new RuntimeException("Falha no upload após múltiplas tentativas.");
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
