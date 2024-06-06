package br.com.foxconcursos.dto;

import java.util.UUID;

public class AbrirRecursoRequest {
    
    private UUID questaoId;
    private String fundamentacao;

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public String getFundamentacao() {
        return fundamentacao;
    }

    public void setFundamentacao(String fundamentacao) {
        this.fundamentacao = fundamentacao;
    }

}
