package br.com.foxconcursos.domain;

public enum StatusRecurso {
    
    EM_ANALISE("Em análise"),
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");

    private String descricao;

    StatusRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
