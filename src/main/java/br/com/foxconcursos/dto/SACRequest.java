package br.com.foxconcursos.dto;

public class SACRequest {

    private String nome;
    private String email;
    private String celular;
    private String produto;
    private String cidadeCargo;
    private String endereco;
    private String observacoes;


    public SACRequest() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getCidadeCargo() {
        return cidadeCargo;
    }

    public void setCidadeCargo(String cidadeCargo) {
        this.cidadeCargo = cidadeCargo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}