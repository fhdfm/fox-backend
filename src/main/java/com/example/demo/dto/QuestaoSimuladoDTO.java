package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.QuestaoSimulado;

public class QuestaoSimuladoDTO {
    
    private UUID id;
    private UUID simuladoId;
    private UUID disciplinaId;
    private String nomeDisciplina;
    private String enunciado;
    private Integer ordem;
    private Integer gabarito;
    private List<ItemQuestaoSimuladoDTO> items;

    public QuestaoSimuladoDTO() {

    }

    public QuestaoSimuladoDTO(QuestaoSimulado questaoSimulado) {
        this.id = questaoSimulado.getId();
        this.simuladoId = questaoSimulado.getSimuladoId();
        this.disciplinaId = questaoSimulado.getDisciplinaId();
        this.enunciado = questaoSimulado.getEnunciado();
        this.ordem = questaoSimulado.getOrdem();
        this.gabarito = questaoSimulado.getGabarito();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSimuladoId() {
        return simuladoId;
    }

    public void setSimuladoId(UUID simuladoId) {
        this.simuladoId = simuladoId;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getGabarito() {
        return gabarito;
    }

    public void setGabarito(Integer gabarito) {
        this.gabarito = gabarito;
    }

    public List<ItemQuestaoSimuladoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemQuestaoSimuladoDTO> items) {
        this.items = items;
    }
}
