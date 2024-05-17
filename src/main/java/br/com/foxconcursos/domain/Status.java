package br.com.foxconcursos.domain;

public enum Status {
    
    ATIVO("A"), 
    INATIVO("I");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
