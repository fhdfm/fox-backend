package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.ItemQuestaoSimulado;

public class ItemQuestaoSimuladoDTO {
    
    private UUID id;
    private UUID questaoSimuladoId;
    private Integer ordem;
    private String descricao;

    public ItemQuestaoSimuladoDTO() {

    }

    public ItemQuestaoSimuladoDTO(ItemQuestaoSimulado itemQuestaoSimulado) {
        this.id = itemQuestaoSimulado.getId();
        this.questaoSimuladoId = itemQuestaoSimulado.getQuestaoSimuladoId();
        this.ordem = itemQuestaoSimulado.getOrdem();
        this.descricao = itemQuestaoSimulado.getDescricao();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UUID getQuestaoSimuladoId() {
        return questaoSimuladoId;
    }

    public void setQuestaoSimuladoId(UUID questaoSimuladoId) {
        this.questaoSimuladoId = questaoSimuladoId;
    }
}
