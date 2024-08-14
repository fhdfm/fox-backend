package br.com.foxconcursos.dto;

import java.util.Date;
import java.util.UUID;
public class ComentarioResponse {
    
    private UUID usuarioId;
    private UUID comentarioId;
    private String usuario;
    private String descricao;
    private Date data;

    public ComentarioResponse() {}

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getData() {
        return this.data;
    }

    public UUID getComentarioId() {
        return comentarioId;
    }

    public void setComentarioId(UUID comentarioId) {
        this.comentarioId = comentarioId;
    }
}
