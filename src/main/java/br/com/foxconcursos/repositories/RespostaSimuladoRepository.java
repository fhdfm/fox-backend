package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;

import br.com.foxconcursos.domain.RespostaSimulado;
import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface RespostaSimuladoRepository extends CustomCrudRepository<RespostaSimulado, UUID> {

    @Query("SELECT * FROM respostas_simulado WHERE simulado_id = :simuladoId AND usuario_id = :usuarioId LIMIT 1")
    Optional<RespostaSimulado> findBySimuladoIdAndUsuarioId(UUID simuladoId, UUID usuarioId);
    
    RespostaSimulado findBySimuladoIdAndUsuarioIdAndStatus(UUID simuladoId, UUID usuarioId, StatusSimulado status);
    
    List<RespostaSimulado> findBySimuladoIdAndStatus(UUID simuladoId, StatusSimulado emAndamento);

}
