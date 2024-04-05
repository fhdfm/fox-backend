package com.example.demo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.example.demo.domain.Banca;
import com.example.demo.domain.Curso;
import com.example.demo.domain.Escolaridade;
import com.example.demo.domain.Status;

public class CursoDTO implements Serializable {
    
    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    private Status status;
    private UUID idBanca;
    private String nomeBanca;
    private Escolaridade escolaridade;
    private String estado;
    private String cidade;
    private BigDecimal valor;

    public CursoDTO() {
    }

    public CursoDTO(Curso curso) {
        this(curso, null);
    }

    public CursoDTO(Curso curso, Banca banca) {
        this.id = curso.getId();
        this.titulo = curso.getTitulo();
        this.descricao = curso.getDescricao();
        this.dataInicio = curso.getDataInicio();
        this.dataTermino = curso.getDataTermino();
        this.status = curso.getStatus();
        this.idBanca = banca != null ? banca.getId() : null;
        this.nomeBanca = banca != null ? banca.getNome() : null;
        this.escolaridade = curso.getEscolaridade();
        this.estado = curso.getEstado();
        this.cidade = curso.getCidade();
        this.valor = curso.getValor();
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

    public UUID getIdBanca() {
        return idBanca;
    }

    public void setIdBanca(UUID idBanca) {
        this.idBanca = idBanca;
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

    public static Page<CursoDTO> toDTOList(Page<Curso> cursos) {

        Page<CursoDTO> pageDTO = cursos.map(new Function<Curso, CursoDTO>() {
            public CursoDTO apply(Curso curso) {
                return new CursoDTO(curso);
            }
        });

        return pageDTO;

    }
}


