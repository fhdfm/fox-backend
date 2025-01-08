package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class AulaResponse {
    
    private UUID id;
    private String titulo;
    private String curso;
    private String disciplina;
    private boolean finalizada;
    private List<ConteudoResponse> conteudo;

    public AulaResponse() {}

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCurso() {
        return this.curso;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getDisciplina() {
        return this.disciplina;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public boolean getFinalizada() {
        return this.finalizada;
    }

    public void setConteudo(List<ConteudoResponse> conteudo) {
        this.conteudo = conteudo;
    }

    public List<ConteudoResponse> getConteudo() {
        return this.conteudo;
    }

}
