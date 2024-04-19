package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.ItemQuestaoSimulado;
import com.example.demo.domain.QuestaoSimulado;
import com.example.demo.dto.ItemQuestaoSimuladoDTO;
import com.example.demo.dto.QuestaoResponse;
import com.example.demo.dto.QuestaoSimuladoDTO;
import com.example.demo.repositories.QuestaoSimuladoRepository;

@Service
public class QuestaoSimuladoService {
    
    private final QuestaoSimuladoRepository questaoSimuladoRepository;
    private final ItemQuestaoSimuladoService itemQuestaoSimuladoService;

    public QuestaoSimuladoService(QuestaoSimuladoRepository questaoSimuladoRepository, 
        ItemQuestaoSimuladoService itemQuestaoSimuladoService) {
        this.questaoSimuladoRepository = questaoSimuladoRepository;
        this.itemQuestaoSimuladoService = itemQuestaoSimuladoService;
    }

    public QuestaoSimuladoDTO findById(UUID id) {
        QuestaoSimulado questaoSimulado = this.questaoSimuladoRepository.findById(id).get();
        QuestaoSimuladoDTO questaoDTO = new QuestaoSimuladoDTO(questaoSimulado);
        List<ItemQuestaoSimulado> items = 
            this.itemQuestaoSimuladoService.findByQuestaoSimuladoId(id);
        List<ItemQuestaoSimuladoDTO> itemsDTO = new ArrayList<ItemQuestaoSimuladoDTO>();
        for (ItemQuestaoSimulado item : items) {
            ItemQuestaoSimuladoDTO itemDTO = new ItemQuestaoSimuladoDTO(item);
            itemsDTO.add(itemDTO);
        }
        questaoDTO.setItems(itemsDTO);
        return questaoDTO;
    }

    public List<QuestaoSimuladoDTO> findBySimuladoId(UUID simuladoId) {

        List<QuestaoSimuladoDTO> result = new ArrayList<QuestaoSimuladoDTO>();

        List<QuestaoSimulado> questoesSimulado = this.questaoSimuladoRepository.findBySimuladoIdOrderByOrdem(simuladoId);
        // TODO: Implementar a busca do nome da disciplina
        for (QuestaoSimulado questaoSimulado : questoesSimulado) {
            QuestaoSimuladoDTO questaoDTO = new QuestaoSimuladoDTO(questaoSimulado);
            
            List<ItemQuestaoSimulado> items = 
                this.itemQuestaoSimuladoService.findByQuestaoSimuladoId(
                    questaoSimulado.getId());
            List<ItemQuestaoSimuladoDTO> itemsDTO = new ArrayList<ItemQuestaoSimuladoDTO>();
            for (ItemQuestaoSimulado item : items) {
               ItemQuestaoSimuladoDTO itemDTO = new ItemQuestaoSimuladoDTO(item);
               itemsDTO.add(itemDTO);
            }
            questaoDTO.setItems(itemsDTO);
            result.add(questaoDTO);
        }

        return result;
    }

    public UUID save(QuestaoSimuladoDTO questaoSimuladoDTO) {
        validarQuestaoSimulado(questaoSimuladoDTO);
        QuestaoSimulado questaoSimulado = new QuestaoSimulado(questaoSimuladoDTO);
        questaoSimulado = this.questaoSimuladoRepository.save(questaoSimulado);
        UUID newQuestaoId = questaoSimulado.getId();
        questaoSimuladoDTO.getItems().forEach(item -> {
            ItemQuestaoSimulado itemQuestaoSimulado = new ItemQuestaoSimulado(item);
            itemQuestaoSimulado.setQuestaoSimuladoId(newQuestaoId);
            this.itemQuestaoSimuladoService.save(itemQuestaoSimulado);
        });
        return newQuestaoId;
    }

    private void validarQuestaoSimulado(QuestaoSimuladoDTO questaoSimuladoDTO) {
        // Implementar validações
    }

    public List<QuestaoResponse> findQuestoesBySimuladoIdAndDisciplinaId(UUID simuladoId, UUID disciplinaId) {
        
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
