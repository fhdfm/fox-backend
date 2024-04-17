package com.example.demo.dto;

import java.util.UUID;

public class MatriculaRequest {
    
    private UUID produtoId; // curso ou simulado
    private UUID usuarioId;

    public MatriculaRequest() {
        
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

}
