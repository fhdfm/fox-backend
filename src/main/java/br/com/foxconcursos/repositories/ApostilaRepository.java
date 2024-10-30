package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Apostila;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface ApostilaRepository extends CustomCrudRepository<Apostila, UUID> {
    
}
