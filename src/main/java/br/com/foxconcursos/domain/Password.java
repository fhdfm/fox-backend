package br.com.foxconcursos.domain;

import java.util.UUID;

public class Password {

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