package br.com.foxconcursos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.domain.ItemQuestaoSimulado;
import br.com.foxconcursos.domain.QuestaoSimulado;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoRequest;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.QuestoesSimuladoDisciplinaResponse;
import br.com.foxconcursos.repositories.QuestaoSimuladoRepository;

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

    public QuestaoSimuladoResponse findById(UUID id) {
        
        QuestaoSimulado questaoSimulado =
            this.questaoSimuladoRepository.findById(id).orElseThrow(() -> 
                new IllegalArgumentException("Questão não encontrada: " + id));

        List<ItemQuestaoSimulado> items = 
            itemQuestaoSimuladoService.findByQuestaoSimuladoId(id);
        
        QuestaoSimuladoResponse response = new QuestaoSimuladoResponse(questaoSimulado, items);
        
        return response;
    }

    public QuestoesSimuladoDisciplinaResponse findBySimuladoId(
        UUID cursoId, UUID simuladoId) {

        List<Disciplina> disciplinas = disciplinaService.findByCursoId(cursoId);

        List<DisciplinaQuestoesResponse> agrupamento =
            new ArrayList<DisciplinaQuestoesResponse>();

        for (Disciplina disciplina : disciplinas) {
           List<QuestaoSimuladoResponse> questoes = 
            findQuestoesBySimuladoIdAndDisciplinaId(simuladoId, disciplina.getId());
            agrupamento.add(new DisciplinaQuestoesResponse(disciplina, questoes));
        }

        QuestoesSimuladoDisciplinaResponse response = 
            new QuestoesSimuladoDisciplinaResponse(agrupamento);

        return response;
    }

    @Transactional
    public UUID save(UUID simuladoId, QuestaoSimuladoRequest request) {
        QuestaoSimulado questaoSimulado = new QuestaoSimulado(request);
        questaoSimulado.setSimuladoId(simuladoId);
        questaoSimulado = this.questaoSimuladoRepository.save(questaoSimulado);
        UUID newQuestaoId = questaoSimulado.getId();
        request.getAlternativas().forEach(item -> {
            ItemQuestaoSimulado itemQuestaoSimulado = new ItemQuestaoSimulado(item);
            itemQuestaoSimulado.setQuestaoSimuladoId(newQuestaoId);
            this.itemQuestaoSimuladoService.save(itemQuestaoSimulado);
        });

        return newQuestaoId;
    }

    @Transactional
    public QuestaoSimuladoResponse save(
        UUID simuladoId, UUID questaoId, QuestaoSimuladoRequest request) {
        
        QuestaoSimulado questaoSimulado = 
        questaoSimuladoRepository.findById(questaoId).orElseThrow(
            () -> new IllegalArgumentException("Questão não encontrada: " + questaoId));
        
        questaoSimulado.setEnunciado(request.getEnunciado());
        questaoSimulado.setOrdem(request.getOrdem());
        questaoSimulado.setDisciplinaId(request.getDisciplinaId());
        questaoSimulado.setSimuladoId(simuladoId);

        request.getAlternativas().forEach(item -> {
            
            ItemQuestaoSimulado itemQuestaoSimulado =
                itemQuestaoSimuladoService.findById(item.getId());
            itemQuestaoSimulado.setQuestaoSimuladoId(questaoId);
            itemQuestaoSimulado.setOrdem(item.getOrdem());
            itemQuestaoSimulado.setDescricao(item.getDescricao());
            itemQuestaoSimulado.setCorreta(item.getCorreta());
            
            itemQuestaoSimuladoService.save(itemQuestaoSimulado);
        });

        return findById(questaoId);
    }

    public List<QuestaoSimuladoResponse> findQuestoesBySimuladoIdAndDisciplinaId(
        UUID simuladoId, UUID disciplinaId) {
        return findQuestoesBySimuladoIdAndDisciplinaId(
            simuladoId, disciplinaId, true);
    }

    public List<QuestaoSimuladoResponse> findQuestoesBySimuladoIdAndDisciplinaId(
        UUID simuladoId, UUID disciplinaId, boolean exibirCorreta) {
        
        List<QuestaoSimuladoResponse> result = new ArrayList<QuestaoSimuladoResponse>();
        
        List<QuestaoSimulado> questoesSimulado = 
            this.questaoSimuladoRepository.findBySimuladoIdAndDisciplinaIdOrderByOrdem(
                simuladoId, disciplinaId);
        
        for (QuestaoSimulado questaoSimulado : questoesSimulado) {
            List<ItemQuestaoSimulado> items = null;
            if (exibirCorreta) {
                items = this.itemQuestaoSimuladoService.findByQuestaoSimuladoId(questaoSimulado.getId());
            } else {
                items = this.itemQuestaoSimuladoService.findByQuestaoSimuladoIdAndCorreta(
                    questaoSimulado.getId(), false);
            
            }
            QuestaoSimuladoResponse questaoResponse = new QuestaoSimuladoResponse(questaoSimulado, items);
            result.add(questaoResponse);
        }
        
        return result;
    }

    public void delete(UUID questaoId) {
        this.questaoSimuladoRepository.deleteById(questaoId);
    }
}
