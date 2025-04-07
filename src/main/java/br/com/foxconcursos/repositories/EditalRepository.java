package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Edital;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface EditalRepository extends CustomCrudRepository<Edital, UUID> {
 
    @Query("SELECT * FROM edital WHERE status = 'ATIVO' ORDER BY ano DESC")
    List<Edital> findAllAtivosOrderByAnoDesc();

    @Query("SELECT * FROM edital ORDER BY ano DESC")
    List<Edital> findAllOrderByAnoDesc();

}
