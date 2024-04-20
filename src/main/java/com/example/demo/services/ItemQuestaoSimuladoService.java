package com.example.demo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.ItemQuestaoSimulado;
import com.example.demo.repositories.ItemQuestaoSimuladoRepository;

@Service
public class ItemQuestaoSimuladoService {
    
    private final ItemQuestaoSimuladoRepository itemQuestaoSimuladoRepository;

    public ItemQuestaoSimuladoService(ItemQuestaoSimuladoRepository itemQuestaoSimuladoRepository) {
        this.itemQuestaoSimuladoRepository = itemQuestaoSimuladoRepository;
    }

    public List<ItemQuestaoSimulado> findByQuestaoSimuladoId(UUID questaoSimuladoId) {
        return itemQuestaoSimuladoRepository.findByQuestaoSimuladoIdOrderByOrdem(questaoSimuladoId);
    }

    public void save(ItemQuestaoSimulado itemQuestaoSimulado) {
        itemQuestaoSimuladoRepository.save(itemQuestaoSimulado);
    }

    public ItemQuestaoSimulado findById(UUID id) {
        return itemQuestaoSimuladoRepository.findById(id).get();
    }

}
