package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Comentario;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface ComentarioRepository extends CustomCrudRepository<Comentario, UUID> {
    
}
