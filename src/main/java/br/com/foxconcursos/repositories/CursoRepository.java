package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface CursoRepository extends CustomCrudRepository<Curso, UUID> {

    @SuppressWarnings("all")
    Page<Curso> findAllByStatus(Pageable pageable, Status status);

    Optional<Curso> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    boolean existsByTituloAndIdNot(String titulo, UUID id);
}
