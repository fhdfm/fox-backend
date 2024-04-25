package com.example.demo.domain;

public enum StatusSimulado {
    
    EM_ANDAMENTO("Em andamento"),
    FINALIZADO("Finalizado");

    private final String value;

    StatusSimulado(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
