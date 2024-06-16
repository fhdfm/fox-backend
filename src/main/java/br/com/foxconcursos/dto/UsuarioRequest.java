package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.PerfilUsuario;

public class UsuarioRequest {
    
    private UUID id;
    private String email;
    private String nome;
    private String password;
    private PerfilUsuario perfil;
    private String cpf;
    private String telefone;

    public UsuarioRequest() {
    }

    public UsuarioRequest(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
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

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
