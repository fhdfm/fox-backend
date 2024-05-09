package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.ItemQuestaoSimulado;

public interface ItemQuestaoSimuladoRepository extends ListCrudRepository<ItemQuestaoSimulado, UUID> {
    
    List<ItemQuestaoSimulado> findByQuestaoSimuladoIdOrderByOrdem(UUID questaoSimuladoId);

    @Query("SELECT correta FROM item_questao_simulado WHERE id = :id AND questao_simulado_id = :questaoSimuladoId")
    Boolean obterResposta(UUID id, UUID questaoSimuladoId);
}
