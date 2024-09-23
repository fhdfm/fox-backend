package br.com.foxconcursos.domain;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("respostas_free")
public class RespostaFree {
    
    @Id
    private UUID id;
    private UUID usuarioId;
    private UUID questaoId;
    private UUID alternativaId;
    private LocalDate dataResposta;

    public RespostaFree() {

    }

    public RespostaFree(UUID usuarioId, UUID questaoId, UUID alternativaId, LocalDate dataResposta) {
        this.usuarioId = usuarioId;
        this.questaoId = questaoId;
        this.dataResposta = dataResposta;
        this.alternativaId = alternativaId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public LocalDate getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDate dataResposta) {
        this.dataResposta = dataResposta;
    }

    public void setAlternativaId(UUID alternativaId) {
        this.alternativaId = alternativaId;
    }

    public UUID getAlternativaId() {
        return this.alternativaId;
    }

}
