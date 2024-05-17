package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.QuestaoSimuladoRequest;

@Table("questoes_simulado")
public class QuestaoSimulado {
    
    @Id
    private UUID id;
    private Integer ordem;
    private UUID simuladoId;
    private String enunciado;
    private UUID disciplinaId;

    public QuestaoSimulado() {

    }

    public QuestaoSimulado(QuestaoSimuladoRequest request) {
        this.ordem = request.getOrdem();
        this.enunciado = request.getEnunciado();
        this.disciplinaId = request.getDisciplinaId();
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

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
}
