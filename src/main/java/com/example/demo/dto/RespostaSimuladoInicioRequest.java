package com.example.demo.dto;

import java.util.UUID;

public class RespostaSimuladoInicioRequest {
    
    private UUID usuarioId;

    public RespostaSimuladoInicioRequest() {
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }
}
