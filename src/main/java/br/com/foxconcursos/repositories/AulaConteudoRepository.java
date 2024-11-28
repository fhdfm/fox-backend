package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.AulaConteudo;

@Repository
public interface AulaConteudoRepository extends CrudRepository<AulaConteudo, UUID> {
    
}
