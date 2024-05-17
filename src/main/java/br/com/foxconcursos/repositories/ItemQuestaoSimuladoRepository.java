package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.ItemQuestaoSimulado;

public interface ItemQuestaoSimuladoRepository extends ListCrudRepository<ItemQuestaoSimulado, UUID> {
    
    List<ItemQuestaoSimulado> findByQuestaoSimuladoIdOrderByOrdem(UUID questaoSimuladoId);

    @Query("SELECT correta FROM itens_questao_simulado WHERE id = :id AND questao_simulado_id = :questaoSimuladoId")
    Boolean obterResposta(UUID id, UUID questaoSimuladoId);
}
