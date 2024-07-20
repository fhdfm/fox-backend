package br.com.foxconcursos.dto;

import java.util.List;
import java.util.Map;

public class BancoQuestaoResponse {
    
    private Map<String, String> filtros;
    
    private int totalDePaginas;
    private int totalDeRegistros;
    private String perfil;

    private List<QuestaoResponse> questoes;

    public BancoQuestaoResponse() {
    }

    public int getTotalDePaginas() {
        return totalDePaginas;
    }

    public void setTotalDePaginas(int totalDePaginas) {
        this.totalDePaginas = totalDePaginas;
    }

    public int getTotalDeRegistros() {
        return totalDeRegistros;
    }

    public void setTotalDeRegistros(int totalDeRegistros) {
        this.totalDeRegistros = totalDeRegistros;
    }

    public List<QuestaoResponse> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestaoResponse> questoes) {
        this.questoes = questoes;
    }

    public Map<String, String> getFiltros() {
        return filtros;
    }

    public void setFiltros(Map<String, String> filtros) {
        this.filtros = filtros;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getPerfil() {
        return this.perfil;
    }

}
