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

    // Construtor vazio
    public ApostilaResponse() {}

    // Construtor completo
    public ApostilaResponse(UUID id, String nome, String descricao, String imagem, BigDecimal valor, Status status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.valor = valor;
        this.status = status;
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
}    
