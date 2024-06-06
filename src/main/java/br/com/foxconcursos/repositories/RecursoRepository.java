package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Recurso;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
@SuppressWarnings("all")
public interface RecursoRepository extends CustomCrudRepository<Recurso, UUID> {
    
    @Query("SELECT r.*, s.titulo as simulado FROM recursos r inner join questoes_simulado qs on r.questao_id = qs.id "
        + " inner join simulados s on s.id = qs.simulado_id WHERE r.usuario_id = :usuarioId order by r.data_abertura desc")
    public List<Recurso> findByUsuarioId(UUID usuarioId);
    
    @Query("SELECT r.*, s.titulo as simulado FROM recursos r inner join questoes_simulado qs on r.questao_id = qs.id "
        + " inner join simulados s on s.id = qs.simulado_id WHERE s.id = :simuladoId order by r.data_abertura desc")
    public List<Recurso> findBySimuladoId(UUID simuladoId);

    @Query("SELECT r.*, s.titulo as simulado FROM recursos r inner join questoes_simulado qs on r.questao_id = qs.id "
    + " inner join simulados s on s.id = qs.simulado_id order by r.data_abertura desc")
    public List<Recurso> findAll();

}
