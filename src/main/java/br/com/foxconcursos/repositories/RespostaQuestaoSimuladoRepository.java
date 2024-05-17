package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.RespostaSimuladoQuestao;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface RespostaQuestaoSimuladoRepository extends CustomCrudRepository<RespostaSimuladoQuestao, UUID> {
    
    RespostaSimuladoQuestao findByRespostaSimuladoIdAndQuestaoId(
        UUID respotaSimuladoId, UUID questaoId);

}
