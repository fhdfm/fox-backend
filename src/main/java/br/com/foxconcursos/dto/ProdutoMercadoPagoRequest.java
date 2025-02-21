package br.com.foxconcursos.dto;

import br.com.foxconcursos.domain.TipoProduto;

import java.util.UUID;

public class ProdutoMercadoPagoRequest {
    private UUID uuid;
    private String titulo;
    private Double valor;
    private TipoProduto tipo;
    private int periodo;

    public ProdutoMercadoPagoRequest() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        if (periodo < 1 || periodo > 12) {
            throw new IllegalArgumentException("O período deve estar entre 1 e 12 meses.");
        }
        this.periodo = periodo;
    }
}
