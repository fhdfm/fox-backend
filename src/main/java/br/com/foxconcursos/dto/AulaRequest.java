package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.Aula;

public class AulaRequest {
    
    private UUID cursoId;
    private UUID disciplinaId;
    private UUID assuntoId;
    private String titulo;
    private int ordem;

    public AulaRequest() {}

    public Aula toModel() {
        return new Aula(cursoId, disciplinaId, assuntoId, titulo, ordem);
    }

    // Método validate
    public void validate() {
        if (cursoId == null) {
            throw new IllegalArgumentException("O campo 'curso' é obrigatório.");
        }
        if (disciplinaId == null) {
            throw new IllegalArgumentException("O campo 'disciplina' é obrigatório.");
        }
        if (assuntoId == null) {
            throw new IllegalArgumentException("O campo 'assunto' é obrigatório.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'titulo' é obrigatório.");
        }
        if (ordem <= 0) {
            throw new IllegalArgumentException("O campo 'ordem' deve ser maior que zero.");
        }
    }    

    public UUID getCursoId() {
        return this.cursoId;
    }

    public void setCursoId(UUID cursoId) {
        this.cursoId = cursoId;
    }

    public UUID getDisciplinaId() {
        return this.disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public UUID getAssuntoId() {
        return this.assuntoId;
    }

    public void setAssuntoId(UUID assuntoId) {
        this.assuntoId = assuntoId;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getOrdem() {
        return this.ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

}
