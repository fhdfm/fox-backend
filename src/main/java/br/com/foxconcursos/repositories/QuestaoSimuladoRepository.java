package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.QuestaoSimulado;

public interface QuestaoSimuladoRepository extends ListCrudRepository<QuestaoSimulado, UUID> {

    List<QuestaoSimulado> findBySimuladoIdAndAnuladaOrderByOrdem(UUID simuladoId, Boolean anulada);
    
    List<QuestaoSimulado> findBySimuladoIdAndDisciplinaIdAndAnuladaOrderByOrdem(
        UUID simuladoId, UUID disciplinaId, Boolean anulada);
}
