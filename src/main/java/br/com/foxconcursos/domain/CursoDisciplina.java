package br.com.foxconcursos.domain;

import java.util.UUID;

public class CursoDisciplina {
    
    private UUID cursoId;
    private UUID disciplinaId;

    public CursoDisciplina() {
    }

    public CursoDisciplina(UUID cursoId, UUID disciplinaId) {
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
