package com.example.demo.domain;

public enum Escolaridade {
    
    MEDIO("Nível Médio"),
    SUPERIOR("Nível Superior");

    private final String escolaridade;

    Escolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getEscolaridade() {
        return escolaridade;
    }
}
