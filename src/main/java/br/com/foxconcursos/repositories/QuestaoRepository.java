package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Questao;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface QuestaoRepository extends CustomCrudRepository<Questao, UUID> {

    Optional<Questao> findByIdAndStatus(UUID id, Status status);
    
    List<Questao> findByProcessadoFalse();

    @Query("SELECT id FROM questoes WHERE comentada = false")
    List<UUID> findIdsByComentadaFalse();

}
