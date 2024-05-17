package br.com.foxconcursos.dto;

public class RankingSimuladoResponse {

    private String nome;
    private int acertos;
    private int classificacao;

    public RankingSimuladoResponse() {
    }

    public RankingSimuladoResponse(String nome, int acertos, int classificacao) {
        this.nome = nome;
        this.acertos = acertos;
        this.classificacao = classificacao;
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

}
