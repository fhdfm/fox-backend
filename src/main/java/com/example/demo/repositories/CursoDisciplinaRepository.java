package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.CursoDisciplina;
import com.example.demo.domain.CursoDisciplinaId;

public interface CursoDisciplinaRepository extends ListCrudRepository<CursoDisciplina, CursoDisciplinaId> {
    
    @Query("SELECT * FROM curso_disciplina WHERE curso_id = :cursoId")
    public List<CursoDisciplina> findByCursoId(UUID cursoId);

    @Query("SELECT COUNT(*) > 0 FROM curso_disciplina WHERE curso_id = :cursoId")
    public Boolean existsByCursoId(UUID cursoId);
}
