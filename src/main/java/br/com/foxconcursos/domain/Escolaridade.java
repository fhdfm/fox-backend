package br.com.foxconcursos.domain;

public enum Escolaridade {
    
    TECNICO("Técnico"),
    SUPERIOR_INCOMPLETO("Superior Incompleto"),
    SUPERIOR_COMPLETO("Superior Completo");

    private final String escolaridade;

    Escolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEscolaridade() {
        return escolaridade;
    }
}
