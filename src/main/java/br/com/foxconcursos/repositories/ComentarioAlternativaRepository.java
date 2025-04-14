package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.ComentarioAlternativa;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface ComentarioAlternativaRepository extends CustomCrudRepository<ComentarioAlternativa, UUID> {
    
}
