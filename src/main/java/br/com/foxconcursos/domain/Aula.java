package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.AulaRequest;

@Table("aulas")
public class Aula {

    @Id
    private UUID id;
    private UUID cursoId;
    private UUID disciplinaId;
    private UUID assuntoId;
    private String titulo;
    private int ordem;
    @Version
    private int version;

    // Construtor padrão (necessário para o Spring Data JDBC)
    public Aula() {}

    // Construtor
    public Aula(UUID cursoId, UUID disciplinaId, UUID assuntoId, String titulo, int ordem) {
        this.cursoId = cursoId;
        this.disciplinaId = disciplinaId;
        this.assuntoId = assuntoId;
        this.titulo = titulo;
        this.ordem = ordem;
    }

    public void updateFromRequest(AulaRequest request) {
        this.cursoId = request.getCursoId();
        this.disciplinaId = request.getDisciplinaId();
        this.assuntoId = request.getAssuntoId();
        this.ordem = request.getOrdem();
        this.titulo = request.getTitulo();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCursoId() {
        return cursoId;
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

    public UUID getAssuntoId() {
        return assuntoId;
    }

    public void setAssuntoId(UUID assuntoId) {
        this.assuntoId = assuntoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}
