package br.com.foxconcursos.domain;

public enum StatusPagamento {
    
    PAGO("PAGO"),
    PENDENTE("PENDENTE");

    private String status;

    StatusPagamento(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
