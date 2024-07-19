package br.com.foxconcursos.dto;

public class PerformanceResponse {
    
    private int acertos;
    private int erros;

    public PerformanceResponse(int acertos, int erros) {
        this.acertos = acertos;
        this.erros = erros;
    }

    public int getAcertos() {
        return acertos;
    }

    public int getErros() {
        return erros;
    }

    public int getTotal() {
        return acertos + erros;
    }

}
