package br.com.foxconcursos.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.foxconcursos.domain.TipoProduto;

public class VendasFilterResquest {
    
    private UUID usuario;
    private UUID produto;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoProduto tipo;
    private boolean entrega;
    private boolean enviado;

    public VendasFilterResquest() {
    }

    public UUID getUsuario() {
        return usuario;
    }

    public void setUsuario(UUID usuario) {
        this.usuario = usuario;
    }

    public UUID getProduto() {
        return produto;
    }

    public void setProduto(UUID produto) {
        this.produto = produto;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public boolean isEntrega() {
        return entrega;
    }

    public void setEntrega(boolean entrega) {
        this.entrega = entrega;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }   

}
