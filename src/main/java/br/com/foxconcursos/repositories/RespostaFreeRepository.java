package br.com.foxconcursos.repositories;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.RespostaFree;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface RespostaFreeRepository extends CustomCrudRepository<RespostaFree, UUID> {
    
    Optional<RespostaFree> findByUsuarioIdAndQuestaoIdAndAlternativaIdAndDataResposta(UUID usuarioId, UUID questaoId, UUID alternativaId, LocalDate data);

    @Query("SELECT COUNT(*) FROM respostas_free WHERE usuario_id = :usuarioId AND data_resposta = :dataResposta")
    int countByUsuarioIdAndDataResposta(UUID usuarioId, LocalDate dataResposta);

}
