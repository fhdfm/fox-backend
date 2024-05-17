package br.com.foxconcursos.domain;

public enum StatusUsuario {
    ATIVO("ATIVO"), INATIVO("INATIVO");

    private final String status;

    StatusUsuario(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
