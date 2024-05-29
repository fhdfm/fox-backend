package br.com.foxconcursos.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;

import br.com.foxconcursos.domain.RespostaSimulado;
import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface RespostaSimuladoRepository extends CustomCrudRepository<RespostaSimulado, UUID> {

    RespostaSimulado findBySimuladoIdAndUsuarioId(UUID simuladoId, UUID usuarioId);
    RespostaSimulado findBySimuladoIdAndUsuarioIdAndStatus(UUID simuladoId, UUID usuarioId, StatusSimulado status);

    @Query("SELECT s.id " +
        "FROM simulados s " +
        "JOIN respostas_simulado r ON s.id = r.simulado_id " +
        "WHERE s.data_inicio + (SUBSTRING(s.duracao FROM 1 FOR 2)::INTEGER || ' hours')::INTERVAL " +
        "+ (SUBSTRING(s.duracao FROM 4 FOR 2)::INTEGER || ' minutes')::INTERVAL < :horaAtual " +
        "AND r.status = 'EM_ANDAMENTO'")
    List<UUID> recuperarSimuladosNaoFinalizados(LocalDateTime horaAtual);
    
    List<RespostaSimulado> findBySimuladoIdAndStatus(UUID simuladoId, StatusSimulado emAndamento);

}
