package br.com.foxconcursos.events;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

public class TokenEvent extends ApplicationEvent {
    
    public static final String ADD = "ADD";
    public static final String REMOVE = "REMOVE";

    private UUID usuarioId;
    private Instant dataExpiracao;
    private String tipo;

    public TokenEvent(Object source) {
        super(source);
    }
    
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Instant getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Instant dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isRemove() {
        return REMOVE.equals(this.tipo);
    }

    public boolean isAdd() {
        return ADD.equals(this.tipo);
    }
}
