package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.demo.domain.Curso;
import com.example.demo.domain.Status;

public interface CursoRepository extends PagingAndSortingRepository<Curso, UUID> {

    @SuppressWarnings("all")
    Page<Curso> findAllByStatus(Pageable pageable, Status status);

    Optional<Curso> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Curso save(Curso curso);

    List<Curso> findAllByStatus(Status status);
}
