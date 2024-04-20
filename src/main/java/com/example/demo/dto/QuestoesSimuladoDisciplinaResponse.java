package com.example.demo.dto;

import java.util.List;

public class QuestoesSimuladoDisciplinaResponse {
    
    private List<DisciplinaQuestoesResponse> disciplinas;

    public QuestoesSimuladoDisciplinaResponse(
        List<DisciplinaQuestoesResponse> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<DisciplinaQuestoesResponse> getDisciplinas() {
        return this.disciplinas;
    }

}
