package com.example.demo.dto;

import java.util.List;

public class QuestaoSimuladoAgrupadoDisciplinaResponse {
    
    private List<DisciplinaQuestoesResponse> disciplinas;

    public QuestaoSimuladoAgrupadoDisciplinaResponse(
        List<DisciplinaQuestoesResponse> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<DisciplinaQuestoesResponse> getDisciplinas() {
        return this.disciplinas;
    }

}
