package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.Status;

public class EscolaMilitarResponse {

    private UUID id;
    private String nome;
    private Status status;


    // Construtor vazio
    public EscolaMilitarResponse() {}

    // Construtor completo
    public EscolaMilitarResponse(UUID id, String nome, Status status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
    }

    // Getters e Setters

    public UUID getId() {
        return this.id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
