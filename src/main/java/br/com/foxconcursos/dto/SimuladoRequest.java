package br.com.foxconcursos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class SimuladoRequest {
    
    private String titulo;
    private String descricao;
    private UUID cursoId;
    private UUID bancaId;
    private Integer alternativasPorQuestao;
    private LocalDateTime dataInicio;
    private String duracao;
    private BigDecimal valor;

    public SimuladoRequest() {
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

    public UUID getBancaId() {
        return bancaId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
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
}
