package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.ItemQuestaoSimulado;

public class ItemQuestaoResponse {
    
    private UUID id;
    private UUID questaoId;
    private Integer ordem;
    private String descricao;

    public ItemQuestaoResponse() {
    }

    public ItemQuestaoResponse(ItemQuestaoSimulado item)  {
        this.id = item.getId();
        this.ordem = item.getOrdem();
        this.descricao = item.getDescricao();
        this.questaoId = item.getQuestaoSimuladoId();
    }

    public UUID getId() {
        return id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }
}