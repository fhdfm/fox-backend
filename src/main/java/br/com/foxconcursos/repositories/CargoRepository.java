package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Cargo;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

import java.util.UUID;

public interface CargoRepository extends CustomCrudRepository<Cargo, UUID> {
    
    Boolean existsByNome(String nome);

}
