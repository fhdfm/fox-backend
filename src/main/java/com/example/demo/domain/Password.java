package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("recuperar_senha")
public class Password {

    @Id
    private String token;
    private UUID usuarioId;

    public Password() {
    }

    public Password(String token, UUID usuarioId) {
        this.token = token;
        this.usuarioId = usuarioId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

}