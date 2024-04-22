package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.example.demo.domain.Simulado;
import com.example.demo.domain.TipoProduto;
import com.example.demo.util.FoxUtils;

public class MatriculaSimuladoResponse extends MatriculaAtivaResponse {
    
    private String titulo;
    private Date data;
    private Integer quantidadeQuestoes;
    private String duracao;

    public MatriculaSimuladoResponse() {
    }

    public MatriculaSimuladoResponse(Simulado simulado) {
        setId(simulado.getId());
        setTipoProduto(TipoProduto.SIMULADO);
        this.titulo = simulado.getTitulo();
        LocalDateTime data = simulado.getDataInicio();
        if (data != null)
            this.data = FoxUtils.convertLocalDateTimeToDate(data);
        this.quantidadeQuestoes = simulado.getQuantidadeQuestoes();
        this.duracao = simulado.getDuracao();
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getData() {
        return data;
    }

    public Integer getQuantidadeQuestoes() {
        return quantidadeQuestoes;
    }

    public String getDuracao() {
        return duracao;
    }
}
