package br.com.foxconcursos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.ComentarioRequest;

@Table("comentarios")
public class Comentario {
    
    @Id
    private UUID id;
    private UUID questao_id;
    private UUID usuario_id;
    private String descricao;
    private LocalDateTime data;
    @Version
    private int version;

    public Comentario() {
    }

    public Comentario(ComentarioRequest request) {
        this.descricao = request.getDescricao();
        this.data = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setQuestaoId(UUID questao_id) {
        this.questao_id = questao_id;
    }

    public UUID getQuestaoId() {
        return this.questao_id;
    }

    public UUID getUsuarioId() {
        return this.usuario_id;
    }

    public void setUsuarioId(UUID usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return this.data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

}
