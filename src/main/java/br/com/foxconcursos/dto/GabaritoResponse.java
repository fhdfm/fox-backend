package br.com.foxconcursos.dto;

import java.util.List;

public class GabaritoResponse {
    
    private String titulo;
    private List<GabaritoQuestoesResponse> questoes;

    public GabaritoResponse() {
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<GabaritoQuestoesResponse> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<GabaritoQuestoesResponse> questoes) {
        this.questoes = questoes;
    }

}
