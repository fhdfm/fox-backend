package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.demo.dto.ItemQuestaoSimuladoRequest;

@Table("itens_questao_simulado")
public class ItemQuestaoSimulado {
    
    @Id
    private UUID id;
    private UUID questaoSimuladoId;
    private Integer ordem;
    private String descricao;
    private Boolean correta;

    public ItemQuestaoSimulado() {

    }

    public ItemQuestaoSimulado(ItemQuestaoSimuladoRequest request) {
        this.ordem = request.getOrdem();
        this.descricao = request.getDescricao();
        this.correta = request.getCorreta();
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

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }
}
