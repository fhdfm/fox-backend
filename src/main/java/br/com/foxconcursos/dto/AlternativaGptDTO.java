package br.com.foxconcursos.dto;

import java.util.UUID;

public record AlternativaGptDTO(
    UUID id,
    String letra,
    String descricao
) {}
