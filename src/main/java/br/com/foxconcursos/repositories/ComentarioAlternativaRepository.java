package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import br.com.foxconcursos.domain.ComentarioAlternativa;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface ComentarioAlternativaRepository extends CustomCrudRepository<ComentarioAlternativa, UUID> {

    List<ComentarioAlternativa> findByQuestaoId(UUID questaoId);
    
}
