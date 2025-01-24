package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.QuestaoVideo;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface QuestaoVideoRepository extends CustomCrudRepository<QuestaoVideo, UUID> {

    List<QuestaoVideo> findByQuestaoId(UUID id);
    
    
}
