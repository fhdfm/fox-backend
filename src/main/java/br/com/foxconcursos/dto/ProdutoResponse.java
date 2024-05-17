package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.TipoProduto;

public abstract class ProdutoResponse {
    
    private UUID id;
    private TipoProduto tipoProduto;

    public ProdutoResponse() {
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
