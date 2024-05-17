package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.Matricula;
import br.com.foxconcursos.domain.Status;

public interface MatriculaRepository extends ListCrudRepository<Matricula, UUID> {
 
    List<Matricula> findByUsuarioIdAndStatus(UUID usuarioId, Status status);

}
