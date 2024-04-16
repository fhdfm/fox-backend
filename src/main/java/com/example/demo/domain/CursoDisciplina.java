package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("curso_disciplina")
public class CursoDisciplina {
    
    @Id
    private CursoDisciplinaId id;

    public CursoDisciplina() {
    }

    public CursoDisciplina(UUID cursoId, UUID disciplinaId) {
        this.id = new CursoDisciplinaId(cursoId, disciplinaId);
    }

    public CursoDisciplinaId getId() {
        return this.id;
    }

    public void setId(CursoDisciplinaId id) {
        this.id = id;
    }

}
