package br.com.foxconcursos.domain;

public enum Escolaridade {
    
    TECNICO("Técnico"),
    SUPERIOR_INCOMPLETO("Superior Incompleto"),
    SUPERIOR("Superior Completo"),
    MEDIO("Nível Médio");

    private final String escolaridade;

    Escolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEscolaridade() {
        return escolaridade;
    }
}
