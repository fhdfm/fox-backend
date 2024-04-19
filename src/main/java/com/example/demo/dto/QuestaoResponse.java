package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.ItemQuestaoSimulado;
import com.example.demo.domain.QuestaoSimulado;



public class QuestaoResponse {
    
    private UUID id;
    private UUID simuladoId;
    private UUID disciplinaId;
    private Integer ordem;
    private String enunciado;
    private Integer gabarito;
    private List<ItemQuestaoResponse> alternativas;

    public QuestaoResponse() {
    }

    public QuestaoResponse(QuestaoSimulado questao, List<ItemQuestaoSimulado> alternativas) {
        
        this.id = questao.getId();
        this.simuladoId = questao.getSimuladoId();
        this.disciplinaId = questao.getDisciplinaId();
        this.ordem = questao.getOrdem();
        this.enunciado = questao.getEnunciado();
        this.gabarito = questao.getGabarito();

        List<ItemQuestaoResponse> alternativasResponse = new ArrayList<ItemQuestaoResponse>();
        for (ItemQuestaoSimulado item : alternativas) {
            alternativasResponse.add(new ItemQuestaoResponse(item));
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getSimuladoId() {
        return simuladoId;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public Integer getGabarito() {
        return gabarito;
    }

    public List<ItemQuestaoResponse> getAlternativas() {
        return alternativas;
    }

}
