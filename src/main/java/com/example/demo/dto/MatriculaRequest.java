package com.example.demo.dto;

import java.util.UUID;

import org.springframework.cglib.core.Local;

public class MatriculaRequest {
    
    private UUID produtoId; // curso ou simulado
    private UUID usuarioId;
    private Local data;

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

    public Local getData() {
        return data;
    }

    public void setData(Local data) {
        this.data = data;
    }

}
