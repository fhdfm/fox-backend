package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class CursoAlunoResponse {
    
    private UUID id;
    private String nome;

    private List<DisciplinaAlunoResponse> disciplinas;

    public CursoAlunoResponse() {

    }

    public void setId(UUID id)  {
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

    public void setDisciplinas(List<DisciplinaAlunoResponse> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<DisciplinaAlunoResponse> getDisciplinas() {
        return this.disciplinas;
    }
}
