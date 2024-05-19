package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface SimuladoRepository extends CustomCrudRepository<Simulado, UUID> {
    
    List<Simulado> findByCursoId(UUID cursoId);
    Boolean existsByCursoId(UUID cursoId);

    @Query("select count(s.id) from simulados s inner join questoes_simulado qs on s.id = qs.simulado_id where s.id = :id")
    int obterQuantidadeQuestoes(UUID id);

}
