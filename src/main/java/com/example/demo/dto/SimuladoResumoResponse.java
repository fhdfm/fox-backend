package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import com.example.demo.util.FoxUtils;

public class SimuladoResumoResponse {
    
    private UUID id;
    private String titulo;
    private String curso;
    private Date data;

    public SimuladoResumoResponse() {
    }

    public SimuladoResumoResponse(UUID id, String titulo, String curso, LocalDateTime data) {
        this.id = id;
        this.titulo = titulo;
        this.curso = curso;
        this.data = FoxUtils.convertLocalDateTimeToDate(data);
    }

    public UUID getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getCurso() {
        return this.curso;
    }

    public Date getData() {
        return this.data;
    }
}
