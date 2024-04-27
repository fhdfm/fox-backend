package com.example.demo.dto;

import java.util.UUID;

public class RespostaSimuladoRequest {
    
    private UUID questaoId;
    private UUID itemQuestaoId;

    public RespostaSimuladoRequest() {
    }

    public UUID getQuestaoId() {
        return this.questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public UUID getItemQuestaoId() {
        return this.itemQuestaoId;
    }

    public void setItemQuestaoId(UUID itemQuestaoId) {
        this.itemQuestaoId = itemQuestaoId;
    }

}
