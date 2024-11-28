package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Aula;

@Repository
public interface AulaRepository extends CrudRepository<Aula, UUID> {
    
}
