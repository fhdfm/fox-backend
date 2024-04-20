package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Matricula;
import com.example.demo.domain.Status;

public interface MatriculaRepository extends ListCrudRepository<Matricula, UUID> {
 
    List<Matricula> findByUsuarioIdAndStatus(UUID usuarioId, Status status);

}
