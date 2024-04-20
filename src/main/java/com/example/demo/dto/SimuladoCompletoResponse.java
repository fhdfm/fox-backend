package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Simulado;
import com.example.demo.util.FoxUtils;

public class SimuladoCompletoResponse {
    
    private UUID id;
    private String titulo;
    private String descricao;
    private UUID cursoId;
    private Integer alternativasPorQuestao;
    private Date dataInicio;
    private String duracao;
    private BigDecimal valor;

    private List<DisciplinaQuestoesResponse> disciplinas;

    public SimuladoCompletoResponse() {
    }

    public SimuladoCompletoResponse(Simulado simulado, 
        List<DisciplinaQuestoesResponse> disciplinas) {
        this.id = simulado.getId();
        this.titulo = simulado.getTitulo();
        this.descricao = simulado.getDescricao();
        this.cursoId = simulado.getCursoId();
        this.alternativasPorQuestao = simulado.getAlternativasPorQuestao();
        this.dataInicio = FoxUtils.convertLocalDateTimeToDate(simulado.getDataInicio());
        this.duracao = simulado.getDuracao();
        this.valor = simulado.getValor();
        this.disciplinas = disciplinas;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public UUID getCursoId() {
        return cursoId;
    }

    public Integer getAlternativasPorQuestao() {
        return alternativasPorQuestao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public String getDuracao() {
        return duracao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public List<DisciplinaQuestoesResponse> getDisciplinas() {
        return disciplinas;
    }
}
