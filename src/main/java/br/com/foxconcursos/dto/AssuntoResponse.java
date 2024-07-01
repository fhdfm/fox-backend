package br.com.foxconcursos.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

public class AssuntoResponse {

    private UUID id;
    private String nome;
    private String disciplina;

    public AssuntoResponse() {}

    public AssuntoResponse(UUID id, String nome, String disciplina) {
        this.id = id;
        this.nome = nome;
        this.disciplina = disciplina;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }
}
