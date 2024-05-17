package com.example.demo.dto;

public class AlterarPasswordRequest {
    
    private String token;
    private String novaSenha;

    public AlterarPasswordRequest() {
    }

    public AlterarPasswordRequest(String token, String novaSenha) {
        this.token = token;
        this.novaSenha = novaSenha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
