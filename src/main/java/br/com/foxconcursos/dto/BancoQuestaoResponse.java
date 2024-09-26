package br.com.foxconcursos.dto;

import java.util.List;
import java.util.Map;

public class BancoQuestaoResponse {
    
    private Map<String, String> filtros;
    private int totalDeRegistros;
    private String perfil;
    private int qtdRespondidas;
    private List<QuestaoResponse> questoes;
    public BancoQuestaoResponse() {
    }

    public Map<String, String> getFiltros() {
        return filtros;
    }

    public void setFiltros(Map<String, String> filtros) {
        this.filtros = filtros;
    }

    public int getTotalDeRegistros() {
        return totalDeRegistros;
    }

    public void setTotalDeRegistros(int totalDeRegistros) {
        this.totalDeRegistros = totalDeRegistros;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public int getQtdRespondidas() {
        return qtdRespondidas;
    }

    public void setQtdRespondidas(int qtdRespondidas) {
        this.qtdRespondidas = qtdRespondidas;
    }

    public List<QuestaoResponse> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestaoResponse> questoes) {
        this.questoes = questoes;
    }
}
