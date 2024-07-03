package br.com.foxconcursos.dto;

import java.util.UUID;

public class AlternativaRequest {
    
    private UUID id;
    private String ordem;
    private String descricao;
    private Boolean correta;

    public AlternativaRequest() {

    }

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public boolean setCorreta(Boolean correta) {
        return this.correta = correta;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
