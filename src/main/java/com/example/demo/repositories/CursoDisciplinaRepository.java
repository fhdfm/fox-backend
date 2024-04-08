package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.CursoDisciplina;

public interface CursoDisciplinaRepository extends ListCrudRepository<CursoDisciplina, UUID> {
    
    public List<CursoDisciplina> findByCursoId(UUID cursoId);
}
