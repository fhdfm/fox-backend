package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.ItemQuestaoSimulado;

public interface ItemQuestaoSimuladoRepository extends ListCrudRepository<ItemQuestaoSimulado, UUID> {
    
    List<ItemQuestaoSimulado> findByQuestaoSimuladoIdOrderByOrdem(UUID questaoSimuladoId);

}
