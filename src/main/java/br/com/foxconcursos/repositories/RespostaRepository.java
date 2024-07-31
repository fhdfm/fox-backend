package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.foxconcursos.domain.RespostaSimulado;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Resposta;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface RespostaRepository extends CustomCrudRepository<Resposta, UUID> {

    Resposta findByQuestaoIdAndUsuarioId(UUID questaoId, UUID usuarioId);

}
