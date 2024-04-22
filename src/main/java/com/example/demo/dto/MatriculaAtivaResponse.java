package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.TipoProduto;

public abstract class MatriculaAtivaResponse {
    
    private UUID id;
    private TipoProduto tipoProduto;

    public MatriculaAtivaResponse() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public UUID getId() {
        return id;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

}
