package br.com.foxconcursos.dto;

import java.util.UUID;

public class AlternativaResponse {
    
    private UUID id;
    private String letra;
    private String descricao;

    public AlternativaResponse() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
