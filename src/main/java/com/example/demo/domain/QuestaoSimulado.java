package com.example.demo.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.demo.dto.QuestaoSimuladoDTO;

@Table("questoes_simulado")
public class QuestaoSimulado {
    
    @Id
    private UUID id;
    private Integer ordem;
    private UUID simuladoId;
    private String enunciado;
    private Integer gabarito;
    private UUID disciplinaId;

    public QuestaoSimulado() {

    }

    public QuestaoSimulado(QuestaoSimuladoDTO dto) {
        this.id = dto.getId();
        this.ordem = dto.getOrdem();
        this.simuladoId = dto.getSimuladoId();
        this.enunciado = dto.getEnunciado();
        this.gabarito = dto.getGabarito();
        this.disciplinaId = dto.getDisciplinaId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public UUID getSimuladoId() {
        return simuladoId;
    }

    public void setSimuladoId(UUID simuladoId) {
        this.simuladoId = simuladoId;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public Integer getGabarito() {
        return gabarito;
    }

    public void setGabarito(Integer gabarito) {
        this.gabarito = gabarito;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
}
