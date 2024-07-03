package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class QuestaoResponse {
    
    private UUID id;
    private String enunciado;

    List<AlternativaResponse> alternativas;

    public QuestaoResponse() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public List<AlternativaResponse> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<AlternativaResponse> alternativas) {
        this.alternativas = alternativas;
    }
}
