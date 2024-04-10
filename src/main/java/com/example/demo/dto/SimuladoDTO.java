package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import com.example.demo.domain.Simulado;
import com.example.demo.util.FoxUtils;

public class SimuladoDTO {
    
    private UUID id;
    private String titulo;
    private String descricao;
    private UUID cursoId;
    private String nomeCurso;
    private UUID bancaId;
    private String nomeBanca;
    private Integer alternativasPorQuestao;
    private LocalDateTime dataInicio;
    private Date dataInicioView;
    private String duracao;
    private BigDecimal valor;

    public SimuladoDTO() {
    }

    public SimuladoDTO(Simulado simulado) {
        this.id = simulado.getId();
        this.titulo = simulado.getTitulo();
        this.descricao = simulado.getDescricao();
        this.cursoId = simulado.getCursoId();
        this.alternativasPorQuestao = simulado.getAlternativasPorQuestao();
        this.dataInicio = simulado.getDataInicio();
        if (simulado.getDataInicio() != null) {
            this.dataInicioView = FoxUtils.convertLocalDateTimeToDate(this.dataInicio);
        }
        this.duracao = simulado.getDuracao();
        this.valor = simulado.getValor();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UUID getCursoId() {
        return cursoId;
    }

    public void setCursoId(UUID cursoId) {
        this.cursoId = cursoId;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public Integer getAlternativasPorQuestao() {
        return alternativasPorQuestao;
    }

    public void setAlternativasPorQuestao(Integer alternativasPorQuestao) {
        this.alternativasPorQuestao = alternativasPorQuestao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UUID getBancaId() {
        return bancaId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
    }

    public String getNomeBanca() {
        return nomeBanca;
    }

    public void setNomeBanca(String nomeBanca) {
        this.nomeBanca = nomeBanca;
    }

    public Date getDataInicioView() {
        return dataInicioView;
    }
}
