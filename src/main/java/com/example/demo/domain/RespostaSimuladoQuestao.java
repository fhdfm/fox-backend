package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("respostas_simulado_questao")
public class RespostaSimuladoQuestao {
    
    @Id
    private UUID id;
    private UUID simuladoId;
    private UUID questaoId;
    private UUID itemQuestaoId;
    private boolean correta;

    public RespostaSimuladoQuestao() {

    }

    public RespostaSimuladoQuestao(UUID simuladoId, UUID questaoId, 
        UUID itemQuestaoId, boolean correta) {
        
        this.simuladoId = simuladoId;
        this.questaoId = questaoId;
        this.itemQuestaoId = itemQuestaoId;
        this.correta = correta;

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

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public UUID getItemQuestaoId() {
        return itemQuestaoId;
    }

    public void setItemQuestaoId(UUID itemQuestaoId) {
        this.itemQuestaoId = itemQuestaoId;
    }

    public boolean isCorreta() {
        return correta;
    }

    public void setCorreta(boolean correta) {
        this.correta = correta;
    }
}
