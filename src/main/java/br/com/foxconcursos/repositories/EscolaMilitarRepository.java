package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.EscolaMilitar;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface EscolaMilitarRepository extends CustomCrudRepository<EscolaMilitar, UUID> {

    
}
