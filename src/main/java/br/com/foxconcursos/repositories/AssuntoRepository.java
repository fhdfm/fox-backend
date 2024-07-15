package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

public interface AssuntoRepository extends CustomCrudRepository<Assunto, UUID> {
    Boolean existsByNomeAndDisciplinaId(String nome, UUID id);

    List<Assunto> findByDisciplinaId(UUID disciplinaId);

    Assunto findByIdAndDisciplinaId(UUID id, UUID disciplinaId);

}
