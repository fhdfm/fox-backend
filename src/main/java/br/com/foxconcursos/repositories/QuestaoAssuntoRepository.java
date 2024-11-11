package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QuestaoAssuntoRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public QuestaoAssuntoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(UUID questaoId, UUID assuntoId) {
        jdbcTemplate.update("delete from questao_assunto where questao_id = ?", questaoId);
        jdbcTemplate.update("insert into questao_assunto (questao_id, assunto_id) values (?, ?)", questaoId, assuntoId);
    }

}
