package br.com.foxconcursos.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class PerformanceEvent {
    
    private boolean acertou;
    private LocalDateTime dataResposta;
    private UUID usuarioId;
    private UUID disciplinaId;

    public PerformanceEvent(boolean acertou, LocalDateTime dataResposta, UUID usuarioId, 
        UUID disciplinaId) {
        
        this.acertou = acertou;
        this.dataResposta = dataResposta;
        this.usuarioId = usuarioId;
        this.disciplinaId = disciplinaId;

    }

    public boolean isRepostaCorreta() {
        return acertou;
    }

    public int getMes() {
        return dataResposta.getMonthValue();
    }
    
    public int getAno() {
        return dataResposta.getYear();
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getDisciplinaId() {
        return this.disciplinaId;
    }

}
