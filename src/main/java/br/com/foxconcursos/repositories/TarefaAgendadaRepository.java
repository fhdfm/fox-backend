package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.TarefaAgendada;
import br.com.foxconcursos.domain.TipoTarefaAgendada;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface TarefaAgendadaRepository extends CustomCrudRepository<TarefaAgendada, UUID> {
    
    @Query("SELECT * FROM tarefas_agendadas WHERE status = 'AGUARDANDO_EXECUCAO'")
    List<TarefaAgendada> carregarTarefasParaAgendamento();

    Optional<TarefaAgendada> findByTargetIdAndTipo(UUID targetId, TipoTarefaAgendada tipo);
}
