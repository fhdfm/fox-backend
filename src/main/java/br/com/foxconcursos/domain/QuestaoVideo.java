package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.AulaVideoResponse;

@Table("aula_video")
public class QuestaoVideo {

    @Id
    private UUID id;

    private UUID questaoId;

    private String video;

    @Version
    private int version;

    public QuestaoVideo(UUID questaoId, String video) {
        this.questaoId = questaoId;
        this.video = video;
    }

    public AulaVideoResponse toResponse() {
        return new AulaVideoResponse(id, questaoId, video);
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
}
