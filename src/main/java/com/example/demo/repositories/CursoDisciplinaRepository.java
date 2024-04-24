package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.CursoDisciplina;

@Repository
public class CursoDisciplinaRepository {

    private final JdbcTemplate jdbcTemplate;

    public CursoDisciplinaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    

    public void save(CursoDisciplina cursoDisciplina) {
        jdbcTemplate.update(
            "INSERT INTO curso_disciplina (curso_id, disciplina_id) VALUES (?, ?)",
            cursoDisciplina.getCursoId(),
            cursoDisciplina.getDisciplinaId()
        );
    }

    public List<CursoDisciplina> findByCursoId(UUID cursoId) {
        return jdbcTemplate.query(
            "SELECT * FROM curso_disciplina WHERE curso_id = ?",
            (rs, rowNum) -> new CursoDisciplina(
                UUID.fromString(rs.getString("curso_id")),
                UUID.fromString(rs.getString("disciplina_id"))
            ),
            cursoId
        );
    }

    public Boolean existsByCursoId(UUID cursoId) {
        return jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM curso_disciplina WHERE curso_id = ?)",
            Boolean.class,
            cursoId
        );
    }

    public void deleteByCursoId(UUID cursoId) {
        jdbcTemplate.update(
            "DELETE FROM curso_disciplina WHERE curso_id = ?",
            cursoId
        );
    }

    public void deleteByCursoIdAndDisciplinaId(UUID cursoId, UUID disciplinaId) {
        jdbcTemplate.update(
            "DELETE FROM curso_disciplina WHERE curso_id = ? AND disciplina_id = ?",
            cursoId,
            disciplinaId
        );
    }
}
