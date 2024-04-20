package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Disciplina;
import com.example.demo.domain.ItemQuestaoSimulado;
import com.example.demo.domain.QuestaoSimulado;
import com.example.demo.dto.DisciplinaQuestoesResponse;
import com.example.demo.dto.QuestaoResponse;
import com.example.demo.dto.QuestaoSimuladoAgrupadoDisciplinaResponse;
import com.example.demo.dto.QuestaoSimuladoRequest;
import com.example.demo.repositories.QuestaoSimuladoRepository;

@Service
public class QuestaoSimuladoService {
    
    private final QuestaoSimuladoRepository questaoSimuladoRepository;
    private final ItemQuestaoSimuladoService itemQuestaoSimuladoService;
    private final DisciplinaService disciplinaService;

    public QuestaoSimuladoService(
        QuestaoSimuladoRepository questaoSimuladoRepository, 
        ItemQuestaoSimuladoService itemQuestaoSimuladoService,
        DisciplinaService disciplinaService) {
        this.questaoSimuladoRepository = questaoSimuladoRepository;
        this.itemQuestaoSimuladoService = itemQuestaoSimuladoService;
        this.disciplinaService = disciplinaService;
    }

    public QuestaoResponse findById(UUID id) {
        
        QuestaoSimulado questaoSimulado =
            this.questaoSimuladoRepository.findById(id).orElseThrow(() -> 
                new IllegalArgumentException("Questão não encontrada: " + id));

        List<ItemQuestaoSimulado> items = 
            itemQuestaoSimuladoService.findByQuestaoSimuladoId(id);
        
        QuestaoResponse response = new QuestaoResponse(questaoSimulado, items);
        
        return response;
    }

    public QuestaoSimuladoAgrupadoDisciplinaResponse findBySimuladoId(
        UUID cursoId, UUID simuladoId) {

        List<Disciplina> disciplinas = disciplinaService.findByCursoId(cursoId);

        List<DisciplinaQuestoesResponse> agrupamento =
            new ArrayList<DisciplinaQuestoesResponse>();

        for (Disciplina disciplina : disciplinas) {
           List<QuestaoResponse> questoes = 
            findQuestoesBySimuladoIdAndDisciplinaId(simuladoId, disciplina.getId());
            agrupamento.add(new DisciplinaQuestoesResponse(disciplina, questoes));
        }

        QuestaoSimuladoAgrupadoDisciplinaResponse response = 
            new QuestaoSimuladoAgrupadoDisciplinaResponse(agrupamento);

        return response;
    }

    @Transactional
    public UUID save(UUID simuladoId, QuestaoSimuladoRequest request) {
        QuestaoSimulado questaoSimulado = new QuestaoSimulado(request);
        questaoSimulado.setSimuladoId(simuladoId);
        questaoSimulado = this.questaoSimuladoRepository.save(questaoSimulado);
        UUID newQuestaoId = questaoSimulado.getId();
        request.getRespostas().forEach(item -> {
            ItemQuestaoSimulado itemQuestaoSimulado = new ItemQuestaoSimulado(item);
            itemQuestaoSimulado.setQuestaoSimuladoId(newQuestaoId);
            this.itemQuestaoSimuladoService.save(itemQuestaoSimulado);
        });
        return newQuestaoId;
    }

    @Transactional
    public QuestaoResponse save(
        UUID simuladoId, UUID questaoId, QuestaoSimuladoRequest request) {
        
        QuestaoSimulado questaoSimulado = 
        questaoSimuladoRepository.findById(questaoId).orElseThrow(
            () -> new IllegalArgumentException("Questão não encontrada: " + questaoId));
        
        questaoSimulado.setEnunciado(request.getEnunciado());
        questaoSimulado.setOrdem(request.getOrdem());
        questaoSimulado.setDisciplinaId(request.getDisciplinaId());
        questaoSimulado.setSimuladoId(simuladoId);

        request.getRespostas().forEach(item -> {
            
            ItemQuestaoSimulado itemQuestaoSimulado =
                itemQuestaoSimuladoService.findById(item.getId());
            itemQuestaoSimulado.setQuestaoSimuladoId(questaoId);
            itemQuestaoSimulado.setOrdem(item.getOrdem());
            itemQuestaoSimulado.setDescricao(item.getDescricao());
            
            itemQuestaoSimuladoService.save(itemQuestaoSimulado);
        });

        return findById(questaoId);
    }

    public List<QuestaoResponse> findQuestoesBySimuladoIdAndDisciplinaId(
        UUID simuladoId, UUID disciplinaId) {
        
        List<QuestaoResponse> result = new ArrayList<QuestaoResponse>();
        
        List<QuestaoSimulado> questoesSimulado = 
            this.questaoSimuladoRepository.findBySimuladoIdAndDisciplinaIdOrderByOrdem(
                simuladoId, disciplinaId);
        
        for (QuestaoSimulado questaoSimulado : questoesSimulado) {
            List<ItemQuestaoSimulado> items = 
                this.itemQuestaoSimuladoService.findByQuestaoSimuladoId(
                    questaoSimulado.getId());
            QuestaoResponse questaoResponse = new QuestaoResponse(questaoSimulado, items);
            result.add(questaoResponse);
        }
        
        return result;
    }
}
