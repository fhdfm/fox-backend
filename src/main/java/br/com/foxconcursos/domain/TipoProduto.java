package br.com.foxconcursos.domain;

public enum TipoProduto {
    
    CURSO("CURSO"),
    SIMULADO("SIMULADO");

    private String tipo;

    TipoProduto(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
