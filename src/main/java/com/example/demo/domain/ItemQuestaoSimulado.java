package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.demo.dto.ItemQuestaoSimuladoDTO;

@Table("itens_questao_simulado")
public class ItemQuestaoSimulado {
    
    @Id
    private UUID id;
    private UUID questaoSimuladoId;
    private Integer ordem;
    private String descricao;

    public ItemQuestaoSimulado() {

    }

    public ItemQuestaoSimulado(ItemQuestaoSimuladoDTO dto) {
        this.id = dto.getId();
        this.ordem = dto.getOrdem();
        this.descricao = dto.getDescricao();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestaoSimuladoId() {
        return questaoSimuladoId;
    }

    public void setQuestaoSimuladoId(UUID questaoSimuladoId) {
        this.questaoSimuladoId = questaoSimuladoId;
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