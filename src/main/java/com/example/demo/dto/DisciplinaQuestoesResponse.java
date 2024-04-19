package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Disciplina;

public class DisciplinaQuestoesResponse {
    
    private UUID id;
    private String nome;
    private List<QuestaoResponse> questoes;

    public DisciplinaQuestoesResponse() {
    }

    public DisciplinaQuestoesResponse(Disciplina disciplina, 
        List<QuestaoResponse> questoes) {    
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

    public List<QuestaoResponse> getQuestoes() {
        return questoes;
    }

}
