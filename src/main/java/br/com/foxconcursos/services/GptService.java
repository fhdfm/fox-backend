package br.com.foxconcursos.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.dto.QuestaoParaGptDTO;

@Service
public class GptService {
    
    private final ComentarioAlternativaService comentarioAlternativaService;
    private final QuestaoService questaoService;

    public GptService(ComentarioAlternativaService comentarioAlternativaService, QuestaoService questaoService) {
        this.questaoService = questaoService;
        this.comentarioAlternativaService = comentarioAlternativaService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processarQuestao(UUID questaoId) throws Exception {
       QuestaoParaGptDTO questao = questaoService.findByIdToGptUse(questaoId);
       comentarioAlternativaService.gerarComentario(questao);
       questaoService.marcarComoComentada(questaoId);
    }
}
