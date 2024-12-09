package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.AulaConteudo;

@Repository
public interface AulaConteudoRepository extends CrudRepository<AulaConteudo, UUID> {
    
    List<AulaConteudo> findByAulaId(UUID aulaId);

    @Query("SELECT a.curso_id FROM aula_conteudo ac INNER JOIN aulas a ON a.id = ac.aula_id WHERE ac.file_id = :fileId")
    UUID findCursoIdByFileId(@Param("fileId") String fileId);

}
