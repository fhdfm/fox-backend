package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class QuestaoSimuladoRequest {
    
    private Integer ordem;
    private String enunciado;
    private UUID disciplinaId;
    private List<ItemQuestaoSimuladoRequest> alternativas;
    private boolean recalcular = false;

    public QuestaoSimuladoRequest() {
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public List<ItemQuestaoSimuladoRequest> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<ItemQuestaoSimuladoRequest> alternativas) {
        this.alternativas = alternativas;
    }

    public void setRecalcular(boolean recalcular) {
        this.recalcular = recalcular;
    }

    public boolean isRecalcular() {
        return this.recalcular;
    }
}