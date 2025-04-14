package br.com.foxconcursos.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ComentarioResposta(
    String questaoId,
    String modeloUtilizado,
    List<Comentario> comentarios
) {
    public record Comentario(
        String alternativaId,
        String alternativa,
        String comentario,
        boolean correta
    ) {}
}