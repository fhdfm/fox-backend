package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public record QuestaoParaGptDTO(UUID idQuestao, String enunciado, List<AlternativaGptDTO> alternativas) {}
