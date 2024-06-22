package br.com.foxconcursos.events;

import org.springframework.context.ApplicationEvent;

import br.com.foxconcursos.domain.Simulado;

public class SimuladoEvent extends ApplicationEvent {

    private Simulado simulado;

    public SimuladoEvent(Object source, Simulado simulado) {
        super(source);
        this.simulado = simulado;
    }
    
    public Simulado getSimulado() {
        return simulado;
    }
}
