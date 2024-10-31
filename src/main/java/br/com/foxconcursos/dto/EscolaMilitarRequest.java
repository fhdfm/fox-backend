package br.com.foxconcursos.dto;

import br.com.foxconcursos.domain.EscolaMilitar;
import br.com.foxconcursos.domain.Status;

public class EscolaMilitarRequest {

    private String nome;
    private Status status;


    // Construtor vazio
    public EscolaMilitarRequest() {}

    // Construtor completo
    public EscolaMilitarRequest(String nome, Status status) {
        this.nome = nome;
        this.status = status;
    }

    public void validateFields() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'nome' é obrigatório e não está preenchido.");
        }

        if (status == null) {
            throw new IllegalArgumentException("O campo 'status' é obrigatório e não está preenchido.");
        }
    }
    
    public EscolaMilitar toModel() {
        return new EscolaMilitar(nome, status);
    }

    // Getters e Setters

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
