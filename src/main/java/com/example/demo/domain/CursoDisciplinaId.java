package com.example.demo.domain;

import java.util.UUID;

public class CursoDisciplinaId {
    
    private UUID cursoId;
    private UUID disciplinaId;

    public CursoDisciplinaId() {
    }

    public CursoDisciplinaId(UUID cursoId, UUID disciplinaId) {
        this.cursoId = cursoId;
        this.disciplinaId = disciplinaId;
    }

    public UUID getCursoId() {
        return this.cursoId;
    }

    public void setCursoId(UUID cursoId) {
        this.cursoId = cursoId;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
}
