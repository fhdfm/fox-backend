package com.example.demo.repositories;

import java.util.UUID;

import com.example.demo.domain.Disciplina;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface DisciplinaRepository extends CustomCrudRepository<Disciplina, UUID> {
    
    Boolean existsByNome(String nome);

}
