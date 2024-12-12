package br.com.foxconcursos.services;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
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

    public YouTubeResponse upload(StorageInput input) throws IOException, GeneralSecurityException {

        Video metadata = buildVideoMetadata();

        InputStreamContent content = new InputStreamContent(null, input.getInputStream());

        Credential credential = this.auth.getAccessToken();

        YouTube youTube = new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,
            credential)
            .setApplicationName(APPLICATION_NAME)
            .build();

        YouTube.Videos.Insert videoUploader = youTube.videos()
                .insert("snippet,statistics,status", metadata, content);

        MediaHttpUploader uploader = videoUploader.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(false);
        // Listener de progresso
        uploader.setProgressListener(new MediaHttpUploaderProgressListener() {
            @Override
            public void progressChanged(MediaHttpUploader uploader) throws IOException {
                switch (uploader.getUploadState()) {
                    case INITIATION_STARTED:
                        System.out.println("Iniciando o upload...");
                        break;
                    case MEDIA_IN_PROGRESS:
                        System.out.printf("Progresso do upload: %.2f%%%n", uploader.getProgress() * 100);
                        break;
                    case MEDIA_COMPLETE:
                        System.out.println("Upload completo.");
                        break;
                    default:
                        System.out.println("Estado desconhecido do upload.");
                }
            }
        });

        Video newVideo = videoUploader.execute();

        String videoUrl = URL + newVideo.getId();

        ThumbnailDetails thumbnailDetails = newVideo.getSnippet().getThumbnails();
        String thumbnailUrl = thumbnailDetails.getDefault().getUrl();

        return new YouTubeResponse(videoUrl, thumbnailUrl);
    }  

    private enum Acessibilidade {
        PRIVATE, UNLISTED
    }
}
