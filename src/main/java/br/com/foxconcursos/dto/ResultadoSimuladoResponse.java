package br.com.foxconcursos.dto;

import java.util.Date;
import java.util.List;

public class ResultadoSimuladoResponse {
    
    private String nome;
    private Date data;
    private List<RankingSimuladoResponse> ranking;

    public ResultadoSimuladoResponse() {
    }

    public ResultadoSimuladoResponse(String nome, Date data, List<RankingSimuladoResponse> ranking) {
        this.nome = nome;
        this.data = data;
        this.ranking = ranking;
    }

    public String getNome() {
        return nome;
    }

    public Date getData() {
        return data;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<RankingSimuladoResponse> getRanking() {
        return ranking;
    }

    public void setRanking(List<RankingSimuladoResponse> ranking) {
        this.ranking = ranking;
    }

}
