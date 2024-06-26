package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Password;

@Repository
public class PasswordRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public PasswordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(String token, UUID usuarioId) {
        jdbcTemplate.update("INSERT INTO recuperar_password (token, usuario_id) VALUES (?, ?)", token, usuarioId);
    }

    public void update(String token, UUID usuarioId) {
        jdbcTemplate.update("UPDATE recuperar_password SET token = ? where usuario_id = ?", token, usuarioId);
    }

    public void delete(String token) {
        jdbcTemplate.update("DELETE FROM recuperar_password WHERE token = ?", token);
    }

    public Password findByToken(String token) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT token, usuario_id FROM recuperar_password WHERE token = ?",
                (rs, rowNum) -> new Password(
                    rs.getString("token"), 
                    UUID.fromString(rs.getString("usuario_id"))),
                token
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Password findByUsuario(UUID usuarioId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT token, usuario_id FROM recuperar_password WHERE usuario_id = ?",
                (rs, rowNum) -> new Password(
                    rs.getString("token"), 
                    UUID.fromString(rs.getString("usuario_id"))), 
                    usuarioId            
                );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }    

    public List<Password> findAll() {
        return jdbcTemplate.query(
            "SELECT token, usuario_id FROM recuperar_password",
            (rs, rowNum) -> new Password(
                rs.getString("token"), 
                UUID.fromString(rs.getString("usuario_id")))
        );
    }

    public void deleteByUsuarioId(UUID usuarioId) {
        jdbcTemplate.update("DELETE FROM recuperar_password WHERE usuario_id = ?", usuarioId);
    }

}
