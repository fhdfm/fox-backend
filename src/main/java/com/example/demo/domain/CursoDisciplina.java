package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("curso_disciplina")
public class CursoDisciplina {
    
    @Column("curso_id")
    private UUID cursoId;

    @Column("disciplina_id")
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