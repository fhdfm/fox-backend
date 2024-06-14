package br.com.foxconcursos.dto;

public class RankingSimuladoResponse {

    private String nome;
    private String cpf;
    private int acertos;
    private int classificacao;

    public RankingSimuladoResponse() {
    }

    public RankingSimuladoResponse(String nome, int acertos, 
        int classificacao, String cpf) {
        this.nome = nome;
        this.acertos = acertos;
        this.classificacao = classificacao;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public int getAcertos() {
        return acertos;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAcertos(int acertos) {
        this.acertos = acertos;
    }

    public void setClassificacao(int classificacao) {
        this.classificacao = classificacao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

}
