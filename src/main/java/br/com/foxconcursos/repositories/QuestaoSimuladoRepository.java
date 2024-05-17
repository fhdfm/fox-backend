package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.QuestaoSimulado;

public interface QuestaoSimuladoRepository extends ListCrudRepository<QuestaoSimulado, UUID> {

    List<QuestaoSimulado> findBySimuladoIdOrderByOrdem(UUID simuladoId);
    List<QuestaoSimulado> findBySimuladoIdAndDisciplinaIdOrderByOrdem(UUID simuladoId, UUID disciplinaId);
}
