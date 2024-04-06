package com.example.demo.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.demo.dto.SimuladoDTO;

@Table("simulados")
public class Simulado {
    
    @Id
    private UUID id;
    private String titulo;
    private String descricao;
    private UUID cursoId;
    private Integer alternativasPorQuestao;
    private LocalDateTime dataInicio;
    private String duracao;
    private BigDecimal valor;

    public Simulado() {
    }

    public Simulado(SimuladoDTO simuladoDTO) {
        this.id = simuladoDTO.getId();
        this.titulo = simuladoDTO.getTitulo();
        this.descricao = simuladoDTO.getDescricao();
        this.cursoId = simuladoDTO.getCursoId();
        this.alternativasPorQuestao = simuladoDTO.getAlternativasPorQuestao();
        this.dataInicio = simuladoDTO.getDataInicio();
        this.duracao = simuladoDTO.getDuracao();
        this.valor = simuladoDTO.getValor();
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
