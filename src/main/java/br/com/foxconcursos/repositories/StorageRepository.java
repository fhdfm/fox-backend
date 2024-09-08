package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Storage;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface StorageRepository extends CustomCrudRepository<Storage, UUID> {
    
}
