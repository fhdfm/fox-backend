package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import br.com.foxconcursos.domain.Alternativa;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface AlternativaRepository extends CustomCrudRepository<Alternativa, UUID> {

    List<Alternativa> findByQuestaoId(UUID questaoId);
    
}
