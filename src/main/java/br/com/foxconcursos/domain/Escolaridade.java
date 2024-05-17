package br.com.foxconcursos.domain;

public enum Escolaridade {
    
    FUNDAMENTAL("Ensino Fundamental"),
    MEDIO("Ensino Médio"),
    SUPERIOR("Ensino Superior");

    private final String escolaridade;

    Escolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEscolaridade() {
        return escolaridade;
    }
}
