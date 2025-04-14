package br.com.foxconcursos.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("comentario_alternativa")
public class ComentarioAlternativa {

    @Id
    private UUID id;

    private UUID questaoId;
    private UUID alternativaId;
    private String comentario;
    private Boolean correta;
    private String modelo;
    @Version
    private int version;
    private OffsetDateTime criadoEm;

    // Getters e Setters
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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
