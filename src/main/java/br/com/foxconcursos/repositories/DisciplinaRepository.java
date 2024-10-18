package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.domain.TipoQuestao;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface DisciplinaRepository extends CustomCrudRepository<Disciplina, UUID> {
    
    Boolean existsByNomeAndTipo(String nome, TipoQuestao tipo);

}
