package br.com.foxconcursos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("respostas")
public class Resposta {
    
    @Id
    private UUID id;
    private UUID questaoId;
    private UUID alternativaId;
    private UUID usuarioId;
    private Boolean acerto;
    private LocalDateTime data;
    @Version
    private int version;

    public Resposta() {}

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

    public UUID getAlternativaId() {
        return alternativaId;
    }

    public void setAlternativaId(UUID alternativaId) {
        this.alternativaId = alternativaId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Boolean getAcerto() {
        return acerto;
    }

    public void setAcerto(Boolean acerto) {
        this.acerto = acerto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
