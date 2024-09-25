package br.com.foxconcursos.dto;


import java.util.UUID;

public class ResultadoResponse {

    private UUID alternativaCorreta;
    private Boolean correta;
    private int qtdRespondidas;

    public ResultadoResponse() {}

    public UUID getAlternativaCorreta() {
        return alternativaCorreta;
    }

    public void setAlternativaCorreta(UUID alternativaCorreta) {
        this.alternativaCorreta = alternativaCorreta;
    }


    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

    public void setQtdRespondidas(int qtd) {
        this.qtdRespondidas = qtd;
    }

    public int getQtdRespondidas() {
        return this.qtdRespondidas;
    }
}
