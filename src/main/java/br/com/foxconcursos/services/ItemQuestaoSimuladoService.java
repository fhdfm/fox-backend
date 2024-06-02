package br.com.foxconcursos.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.ItemQuestaoSimulado;
import br.com.foxconcursos.repositories.ItemQuestaoSimuladoRepository;

@Service
public class ItemQuestaoSimuladoService {
    
    private final ItemQuestaoSimuladoRepository itemQuestaoSimuladoRepository;

    public ItemQuestaoSimuladoService(ItemQuestaoSimuladoRepository itemQuestaoSimuladoRepository) {
        this.itemQuestaoSimuladoRepository = itemQuestaoSimuladoRepository;
    }

    public List<ItemQuestaoSimulado> findByQuestaoSimuladoId(UUID questaoSimuladoId) {
        return itemQuestaoSimuladoRepository.findByQuestaoSimuladoIdOrderByOrdem(questaoSimuladoId);
    }

    public List<ItemQuestaoSimulado> findByQuestaoSimuladoIdAndCorreta(
        UUID questaoSimuladoId, Boolean exibirCorreta) {
        if (exibirCorreta)
            return this.findByQuestaoSimuladoId(questaoSimuladoId);
        
        List<ItemQuestaoSimulado> itens =
            itemQuestaoSimuladoRepository.findByQuestaoSimuladoIdOrderByOrdem(questaoSimuladoId);
        itens.forEach(item -> item.setCorreta(false));
        return itens;
    }

    public void save(ItemQuestaoSimulado itemQuestaoSimulado) {
        itemQuestaoSimuladoRepository.save(itemQuestaoSimulado);
    }

    public ItemQuestaoSimulado findById(UUID id) {
        return itemQuestaoSimuladoRepository.findById(id).get();
    }

    public Boolean estaCorreta(UUID id, UUID questaoSimuladoId) {
        return itemQuestaoSimuladoRepository.obterResposta(id, questaoSimuladoId);
    }

    public void deleteByQuestaoSimuladoId(UUID questaoId) {
        itemQuestaoSimuladoRepository.deleteByQuestaoSimuladoId(questaoId);
    }

}
