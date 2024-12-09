package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class DisciplinaAlunoResponse {
    
    private UUID id;
    private String nome;

    private List<AulaResponse> aulas;

    public DisciplinaAlunoResponse() {

    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setAulas(List<AulaResponse> aulas) {
        this.aulas = aulas;
    }

    public List<AulaResponse> getAulas() {
        return this.aulas;
    }
}
