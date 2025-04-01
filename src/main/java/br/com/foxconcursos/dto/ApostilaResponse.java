package br.com.foxconcursos.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.foxconcursos.domain.Status;

public class ApostilaResponse {

    private UUID id;
    private String nome;
    private String descricao;
    private String imagem;
    private BigDecimal valor;
    private Status status;
    private String cargo;
    private String cidade;
    private String uf;
    private Boolean pdf;

    // Construtor vazio
    public ApostilaResponse() {}

    // Construtor completo
    public ApostilaResponse(UUID id, String nome, String descricao, String imagem, 
            BigDecimal valor, Status status, String cargo, String cidade, String uf, Boolean pdf) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.valor = valor;
        this.status = status;
        this.cargo = cargo;
        this.cidade = cidade;
        this.uf = uf;
        this.pdf = pdf;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUf() {
        return this.uf;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCargo() {
        return this.cargo;
    }

    public Boolean getPdf() {
        return pdf;
    }

    public void setPdf(Boolean pdf) {
        this.pdf = pdf;
    }

}    
