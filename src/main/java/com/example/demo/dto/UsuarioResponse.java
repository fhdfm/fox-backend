package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.Usuario;
import com.example.demo.util.FoxUtils;

public class UsuarioResponse {
    
    private UUID id;
    private String email;
    private String nome;
    private String cpf;
    private String perfil;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.perfil = usuario.getPerfil().name();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return FoxUtils.formatarCpf(cpf);
    }

    public String getPerfil() {
        return perfil;
    }
}
