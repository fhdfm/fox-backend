package br.com.foxconcursos.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface SimuladoRepository extends CustomCrudRepository<Simulado, UUID> {
    
    List<Simulado> findByCursoId(UUID cursoId);
    
    Boolean existsByCursoId(UUID cursoId);

    @Query("select s.quantidadeQuestoes from simulados s where s.id = :id")
    int obterQuantidadeQuestoes(UUID id);

    @Query("SELECT s.id FROM simulados s " 
      + "WHERE s.data_inicio + (SUBSTRING(s.duracao FROM 1 FOR 2)::INTEGER || ' hours')::INTERVAL + "
      + "(SUBSTRING(s.duracao FROM 4 FOR 2)::INTEGER || ' minutes')::INTERVAL < :horaAtual and s.id = :simuladoId")
    List<UUID> simuladosExpirados(UUID simuladoId, LocalDateTime horaAtual);

}
