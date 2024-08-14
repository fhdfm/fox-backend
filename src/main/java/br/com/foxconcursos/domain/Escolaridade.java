package br.com.foxconcursos.domain;

public enum Escolaridade {
    
    TECNICO("Técnico"),
    FUNDAMENTAL("Ensino Fundamental"),
    SUPERIOR("Ensino Superior"),
    MEDIO("Ensino Médio");

    private final String escolaridade;

    Escolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEscolaridade() {
        return escolaridade;
    }
}
