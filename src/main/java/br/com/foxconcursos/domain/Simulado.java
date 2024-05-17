package br.com.foxconcursos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.SimuladoRequest;

@Table("simulados")
public class Simulado {
    
    @Id
    private UUID id;
    private String titulo;
    private String descricao;
    private UUID cursoId;
    private Integer alternativasPorQuestao;
    private Integer quantidadeQuestoes;
    private LocalDateTime dataInicio;
    private String duracao;
    private BigDecimal valor;

    public Simulado() {
    }

    public Simulado(SimuladoRequest request) {
        this.titulo = request.getTitulo();
        this.descricao = request.getDescricao();
        this.cursoId = request.getCursoId();
        this.alternativasPorQuestao = request.getAlternativasPorQuestao();
        this.dataInicio = request.getDataInicio();
        this.duracao = request.getDuracao();
        this.valor = request.getValor();
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

    public Integer getQuantidadeQuestoes() {
        return quantidadeQuestoes;
    }

    public void setQuantidadeQuestoes(Integer quantidadeQuestoes) {
        this.quantidadeQuestoes = quantidadeQuestoes;
    }

}
