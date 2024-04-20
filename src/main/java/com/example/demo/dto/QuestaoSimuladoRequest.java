package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

public class QuestaoSimuladoRequest {
    
    private Integer ordem;
    private String enunciado;
    private Integer gabarito;
    private UUID disciplinaId;
    private List<ItemQuestaoSimuladoRequest> respostas;

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

    public Integer getGabarito() {
        return gabarito;
    }

    public void setGabarito(Integer gabarito) {
        this.gabarito = gabarito;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public List<ItemQuestaoSimuladoRequest> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<ItemQuestaoSimuladoRequest> respostas) {
        this.respostas = respostas;
    }
}