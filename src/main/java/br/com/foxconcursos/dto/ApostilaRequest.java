package br.com.foxconcursos.dto;

import java.math.BigDecimal;

import br.com.foxconcursos.domain.Apostila;
import br.com.foxconcursos.domain.Status;

public class ApostilaRequest {
    
    private String nome;
    private String descricao;
    private String imagem;
    private BigDecimal valor;
    private Status status;
    private String cidade;
    private String uf;
    private String cargo;

    // Construtor vazio
    public ApostilaRequest() {}

    public ApostilaRequest(String nome, String descricao, String imagem, BigDecimal valor, Status status) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.valor = valor;
        this.status = status;
    }

    public void validateFields() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'nome' é obrigatório e não está preenchido.");
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'descricao' é obrigatório e não está preenchido.");
        }

        if (imagem == null || imagem.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'imagem' é obrigatório e não está preenchido.");
        }

        if (valor == null) {
            throw new IllegalArgumentException("O campo 'valor' é obrigatório e não está preenchido.");
        }

        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'cargo' é obrigatório e não esta preenchido.");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("O campo 'status' é obrigatório e não está preenchido.");
        }
    }
    
    public Apostila toModel() {
        return new Apostila(nome, descricao, imagem, valor, status, cargo, cidade, uf);
    }

    // Getters e Setters

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
        return valor;
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

    public void setCargo(String cargo)  {
        this.cargo = cargo;
    }

    public String getCargo() {
        return this.cargo;
    }

}