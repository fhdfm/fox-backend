package com.example.demo.repositories;

import java.util.UUID;

import com.example.demo.domain.RespostaSimuladoQuestao;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface RespostaQuestaoSimuladoRepository extends CustomCrudRepository<RespostaSimuladoQuestao, UUID> {
    
    RespostaSimuladoQuestao findByRespostaSimuladoIdAndQuestaoId(
        UUID respotaSimuladoId, UUID questaoId);

}
