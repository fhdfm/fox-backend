package br.com.foxconcursos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ComentarioRespostaAPI(
    @JsonProperty("questaoId") String questaoId,
    @JsonProperty("alternativaId") String alternativaId,
    @JsonProperty("alternativa") String alternativa,
    @JsonProperty("comentario") String comentario,
    @JsonProperty("correta") boolean correta
) {
    // Construtor vazio para Jackson
    public ComentarioRespostaAPI() {
        this(null, null, null, null, false);
    }
}