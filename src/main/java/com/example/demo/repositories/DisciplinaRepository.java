package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Disciplina;

public interface DisciplinaRepository extends ListCrudRepository<Disciplina, UUID>{

}
