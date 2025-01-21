package br.com.foxconcursos.dto;

import java.util.UUID;

public class QuestaoVideoResponse {

    private UUID id;

    private UUID questaoId;

    private String video;

    public QuestaoVideoResponse(UUID id, UUID questaoId, String video) {
        this.questaoId = questaoId;
        this.video = video;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
    
}
