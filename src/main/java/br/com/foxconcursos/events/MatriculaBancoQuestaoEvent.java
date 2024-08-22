package br.com.foxconcursos.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatriculaBancoQuestaoEvent {
    
    private UUID matriculaId;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    public MatriculaBancoQuestaoEvent(UUID matriculaId, LocalDateTime inicio, LocalDateTime fim) {
        this.matriculaId = matriculaId;
        this.inicio = inicio;
        this.fim = fim;
    }

    public UUID getMatriculaId() {
        return this.matriculaId;
    }

    public LocalDateTime getInicio() {
        return this.inicio;
    }

    public LocalDateTime getFim() {
        return this.fim;
    }

}
