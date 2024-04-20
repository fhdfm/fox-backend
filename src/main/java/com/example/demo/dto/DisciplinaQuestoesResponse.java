package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Disciplina;

public class DisciplinaQuestoesResponse {
    
    private UUID id;
    private String nome;
    private List<QuestaoSimuladoResponse> questoes;

    public DisciplinaQuestoesResponse() {
    }

    public DisciplinaQuestoesResponse(Disciplina disciplina, 
        List<QuestaoSimuladoResponse> questoes) {    
        this.id = disciplina.getId();
        this.nome = disciplina.getNome();
        this.questoes = questoes;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<QuestaoSimuladoResponse> getQuestoes() {
        return questoes;
    }

}
