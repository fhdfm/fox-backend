package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.domain.Cargo;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

import java.util.UUID;

public interface AssuntoRepository extends CustomCrudRepository<Assunto, UUID> {
    
    Boolean existsByNomeAndDisciplinaId(String nome, UUID id);

}
