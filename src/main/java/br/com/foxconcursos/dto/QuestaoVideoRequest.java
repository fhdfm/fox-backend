package br.com.foxconcursos.dto;

import org.springframework.web.multipart.MultipartFile;

public class QuestaoVideoRequest {

    private MultipartFile video;

    public QuestaoVideoRequest() {

    }

    // Getters and Setters

    public MultipartFile getVideo() {
        return video;
    }

    public void setVideo(MultipartFile video) {
        this.video = video;
    }
    
}
