package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Password;

@Repository
public class PasswordRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public PasswordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String token, UUID usuarioId) {
        jdbcTemplate.update("INSERT INTO recuperar_password (token, usuario_id) VALUES (?, ?)", token, usuarioId);
    }

    public void delete(String token) {
        jdbcTemplate.update("DELETE FROM recuperar_password WHERE token = ?", token);
    }

    public Password findByToken(String token) {
        return jdbcTemplate.queryForObject(
            "SELECT token, usuario_id FROM recuperar_password WHERE token = ?",
            (rs, rowNum) -> new Password(
                rs.getString("token"), 
                UUID.fromString(rs.getString("usuario_id"))),
            token
        );
    }

}
