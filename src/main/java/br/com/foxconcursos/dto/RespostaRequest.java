package br.com.foxconcursos.dto;

import java.util.UUID;

public class RespostaRequest {
    
    private UUID alternativaId;

    public RespostaRequest() {
    }

    public UUID getAlternativaId() {
        return alternativaId;
    }

    public void setAlternativaId(UUID alternativaId) {
        this.alternativaId = alternativaId;
    }
}
