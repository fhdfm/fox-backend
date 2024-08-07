package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;
import org.springframework.data.jdbc.repository.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SimuladoRepository extends CustomCrudRepository<Simulado, UUID> {

    List<Simulado> findByCursoId(UUID cursoId);

    @Query("SELECT s.* " +
            "FROM simulados s " +
            "WHERE s.curso_id = :cursoId " +
            "  AND s.id NOT IN (SELECT m.produto_id FROM matriculas m where usuario_id = :usuarioId) " +
            "ORDER BY s.titulo asc")
    List<Simulado> findSimuladoNaoMatriculadosByCursoId(UUID cursoId,UUID usuarioId ) ;

    Boolean existsByCursoId(UUID cursoId);

    @Query("select s.quantidade_questoes from simulados s where s.id = :id")
    int obterQuantidadeQuestoes(UUID id);

    @Query("SELECT s.id FROM simulados s "
            + "WHERE s.data_inicio + (SUBSTRING(s.duracao FROM 1 FOR 2)::INTEGER || ' hours')::INTERVAL + "
            + "(SUBSTRING(s.duracao FROM 4 FOR 2)::INTEGER || ' minutes')::INTERVAL < :horaAtual and s.id = :simuladoId")
    List<UUID> simuladosExpirados(UUID simuladoId, LocalDateTime horaAtual);

}
