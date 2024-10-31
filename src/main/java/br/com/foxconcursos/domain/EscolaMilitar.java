package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.EscolaMilitarRequest;
import br.com.foxconcursos.dto.EscolaMilitarResponse;

@Table("escola_militar")
public class EscolaMilitar {

    @Id
    private UUID id;
    private String nome;
    private Status status;
    @Version
    private int version;

    // Construtor vazio
    public EscolaMilitar() {}

    // Construtor completo
    public EscolaMilitar(String nome, Status status) {
        this.nome = nome;
        this.status = status;
    }

    public EscolaMilitarResponse toAssembly() {
        return new EscolaMilitarResponse(id, nome, status);
    }

    public void updateFromRequest(EscolaMilitarRequest request) {
        this.setNome(request.getNome());
        this.setStatus(request.getStatus());
    }    

    // Getters e Setters

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EscolaMilitar{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", status='" + status + '\'' +
                ", version=" + version +
                '}';
    }
}
