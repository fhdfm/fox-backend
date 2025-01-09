package br.com.foxconcursos.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.CursoRequest;

@Table("cursos")
public class Curso {

    @Id
    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    private Status status;
    private UUID bancaId;
    private Escolaridade escolaridade;
    private String estado;
    private String cidade;
    private BigDecimal valor;
    private String imagem;

    public Curso() {
    }

    public Curso(CursoRequest request) {
        this.id = request.getId();
        this.titulo = request.getTitulo();
        this.descricao = request.getDescricao();
        this.dataInicio = request.getDataInicio();
        this.dataTermino = request.getDataTermino();
        this.status = request.getStatus();
        this.bancaId = request.getBancaId();
        this.escolaridade = request.getEscolaridade();
        this.estado = request.getEstado();
        this.cidade = request.getCidade();
        this.valor = request.getValor();
    }

    public void updateFromRequest(CursoRequest request) {
        this.titulo = request.getTitulo();
        this.descricao = request.getDescricao();
        this.dataInicio = request.getDataInicio();
        this.dataTermino = request.getDataTermino();
        this.status = request.getStatus();
        this.bancaId = request.getBancaId();
        this.escolaridade = request.getEscolaridade();
        this.estado = request.getEstado();
        this.cidade = request.getCidade();
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

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getImagem() {
        return this.imagem;
    }
}
