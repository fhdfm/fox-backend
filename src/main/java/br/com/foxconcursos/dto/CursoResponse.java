package br.com.foxconcursos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Escolaridade;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.util.FoxUtils;

public class CursoResponse implements Serializable {
    
    private UUID id;
    private String titulo;
    private String descricao;
    private Date dataInicio;
    private Date dataTermino;
    private Status status;
    private UUID bancaId;
    private String banca;
    private Escolaridade escolaridade;
    private String estado;
    private String cidade;
    private BigDecimal valor;
    private String imagem;

    private boolean possuiDisciplinas;

    public CursoResponse() {
    }

    public CursoResponse(Curso curso) {
        this.id = curso.getId();
        this.titulo = curso.getTitulo();
        this.descricao = curso.getDescricao();
        
        LocalDate inicio = curso.getDataInicio();
        if (inicio != null) {
            this.dataInicio = 
                FoxUtils.convertLocalDateToDate(inicio);
        }

        LocalDate termino = curso.getDataTermino();
        if (termino != null) {
            this.dataTermino =
                FoxUtils.convertLocalDateToDate(termino);
        }
        
        this.bancaId = curso.getBancaId();
        this.status = curso.getStatus();
        this.escolaridade = curso.getEscolaridade();
        this.estado = curso.getEstado();
        this.cidade = curso.getCidade();
        this.valor = curso.getValor();
        this.imagem = curso.getImagem();

        this.possuiDisciplinas = false;
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

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UUID getBancaId() {
        return bancaId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
    }

    public String getBanca() {
        return banca;
    }

    public void setBanca(String banca) {
        this.banca = banca;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isPossuiDisciplinas() {
        return possuiDisciplinas;
    }

    public void setPossuiDisciplinas(boolean possuiDisciplinas) {
        this.possuiDisciplinas = possuiDisciplinas;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getImagem() {
        return this.imagem;
    }
}