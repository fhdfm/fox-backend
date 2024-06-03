package br.com.foxconcursos.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import br.com.foxconcursos.util.FoxUtils;

public class SimuladoResumoResponse {
    
    private UUID id;
    private String titulo;
    private String curso;
    private Date data;
    private Date dataFim;

    public SimuladoResumoResponse() {
    }

    public SimuladoResumoResponse(UUID id, String titulo, String curso, LocalDateTime data) {
        this.id = id;
        this.titulo = titulo;
        this.curso = curso;
        this.data = FoxUtils.convertLocalDateTimeToDate(data);
    }

    public SimuladoResumoResponse(UUID id, String titulo, String curso, 
        LocalDateTime data, LocalDateTime dataFim) {
        this.id = id;
        this.titulo = titulo;
        this.curso = curso;
        this.data = FoxUtils.convertLocalDateTimeToDate(data);
        this.dataFim = FoxUtils.convertLocalDateTimeToDate(dataFim);
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

    public Date getDataFim() {
        return this.dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
}
