package br.com.foxconcursos.events;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

public class RecalcularEvent extends ApplicationEvent {
    
    private UUID simuladoId;

    private RecalcularEvent(UUID simuladoId) {
        super(simuladoId);
    }

    public static RecalcularEvent of(UUID simuladoId) {
        return new RecalcularEvent(simuladoId);
    }

    public UUID getSimuladoId() {
        return this.simuladoId;
    }

}
