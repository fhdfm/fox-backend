package com.example.demo.dto;

import java.util.Date;

import com.example.demo.domain.Curso;
import com.example.demo.domain.Escolaridade;
import com.example.demo.domain.Status;
import com.example.demo.domain.TipoProduto;
import com.example.demo.util.FoxUtils;

public class ProdutoCursoResponse extends ProdutoResponse {
    
    private String titulo;
    private String banca;
    private Escolaridade escolaridade;
    private Date dataInicio;
    private Date dataTermino;
    private Status status;

    public ProdutoCursoResponse() {
    }

    public ProdutoCursoResponse(Curso curso, String banca) {
        setId(curso.getId());
        setTipoProduto(TipoProduto.CURSO);
        this.titulo = curso.getTitulo();
        this.banca = banca;
        this.escolaridade = curso.getEscolaridade();
        if (curso.getDataInicio() != null)
            this.dataInicio = FoxUtils.convertLocalDateToDate(curso.getDataInicio());
        if (curso.getDataTermino() != null)
            this.dataTermino = FoxUtils.convertLocalDateToDate(curso.getDataTermino());
        this.status = curso.getStatus();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

}