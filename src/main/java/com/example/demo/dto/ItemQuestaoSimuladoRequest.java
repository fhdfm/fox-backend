package com.example.demo.dto;

import java.util.UUID;

public class ItemQuestaoSimuladoRequest {
    
    private UUID id;
    private Integer ordem;
    private String descricao;

    public ItemQuestaoSimuladoRequest() {
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
}
