package br.com.foxconcursos.dto;

import java.util.UUID;

public class VendasStatusUpdateRequest {
    private UUID vendaId;
    private Boolean enviado;

    public UUID getVendaId() {
        return vendaId;
    }

    public void setVendaId(UUID vendaId) {
        this.vendaId = vendaId;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }
}
