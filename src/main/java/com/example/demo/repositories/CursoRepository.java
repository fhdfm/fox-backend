package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.demo.domain.Curso;

public interface CursoRepository extends PagingAndSortingRepository<Curso, UUID> {

    @SuppressWarnings("all")
    Page<Curso> findAll(Pageable pageable);

    Optional<Curso> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Curso save(Curso curso);
}
