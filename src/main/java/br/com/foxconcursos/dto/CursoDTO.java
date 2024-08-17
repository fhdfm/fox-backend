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

public class CursoDTO implements Serializable {
    
    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    private Date dataInicioView;
    private Date dataTerminoView;
    private Status status;
    private UUID bancaId;
    private String nomeBanca;
    private Escolaridade escolaridade;
    private String estado;
    private String cidade;
    private BigDecimal valor;
    private String imagem;

    private boolean possuiDisciplinas;

    public CursoDTO() {
    }

    public CursoDTO(Curso curso) {
        this.id = curso.getId();
        this.titulo = curso.getTitulo();
        this.descricao = curso.getDescricao();
        this.dataInicio = curso.getDataInicio();
        
        if (curso.getDataInicio() != null) {
            this.dataInicioView = 
                FoxUtils.convertLocalDateToDate(this.dataInicio);
        }

        this.dataTermino = curso.getDataTermino();
        
        if (curso.getDataTermino() != null) {
            this.dataTerminoView =
                FoxUtils.convertLocalDateToDate(this.dataTermino);
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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
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

    public String getNomeBanca() {
        return nomeBanca;
    }

    public void setNomeBanca(String nomeBanca) {
        this.nomeBanca = nomeBanca;
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

    public Date getDataInicioView() {
        return dataInicioView;
    }

    public Date getDataTerminoView() {
        return dataTerminoView;
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