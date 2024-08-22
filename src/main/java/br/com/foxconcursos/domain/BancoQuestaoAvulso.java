package br.com.foxconcursos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("banco_questao_matricula")
public class BancoQuestaoAvulso {
    
    @Id
    private UUID id;
    private UUID matriculaId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    @Version
    private int version;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public void setMatriculaId(UUID matriculaId) {
        this.matriculaId = matriculaId;
    }

    public UUID getMatriculaId() {
        return this.matriculaId;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getInicio() {
        return this.inicio;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public LocalDateTime getFim() {
        return this.fim;
    }

}
