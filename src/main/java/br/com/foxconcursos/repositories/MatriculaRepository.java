package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.Matricula;
import br.com.foxconcursos.domain.Status;

public interface MatriculaRepository extends ListCrudRepository<Matricula, UUID> {
 
    List<Matricula> findByUsuarioIdAndStatus(UUID usuarioId, Status status);

    Optional<Matricula> findByUsuarioIdAndProdutoId(UUID usuarioId, UUID produtoId);


}
