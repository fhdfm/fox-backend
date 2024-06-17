package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.foxconcursos.dto.UsuarioRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Table("usuarios")
public class Usuario {

    @Id
    private UUID id;
    private String email;
    private String nome;
    private String password;
    private PerfilUsuario perfil;
    private String cpf;
    private StatusUsuario status;
    private String telefone;

    public Usuario() {
    }

    public Usuario(UsuarioRequest request) {
        this.id = request.getId();
        this.email = request.getEmail();
        this.nome = request.getNome();
        this.password = request.getPassword();
        this.perfil = request.getPerfil();
        this.cpf = request.getCpf();
        this.telefone = request.getTelefone();
        this.status = StatusUsuario.ATIVO;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setRole(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

}
