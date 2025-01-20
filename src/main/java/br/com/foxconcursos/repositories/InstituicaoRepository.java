package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.domain.Instituicao;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

import java.util.UUID;

public interface InstituicaoRepository extends CustomCrudRepository<Instituicao, UUID> {
    
    Boolean existsByNomeAndTipo(String nome, String tipo);

}
