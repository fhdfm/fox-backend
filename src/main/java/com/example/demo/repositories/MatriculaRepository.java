package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Matricula;

public interface MatriculaRepository extends ListCrudRepository<Matricula, UUID> {
    
}
