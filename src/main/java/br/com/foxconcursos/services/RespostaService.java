package br.com.foxconcursos.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Resposta;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.RespostaRequest;
import br.com.foxconcursos.events.PerformanceEvent;
import br.com.foxconcursos.repositories.RespostaRepository;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class RespostaService {
    
    private final RespostaRepository respostaRepository;
    private final QuestaoService questaoService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RespostaService(RespostaRepository respostaRepository, QuestaoService questaoService, 
        ApplicationEventPublisher applicationEventPublisher) {
        
        this.respostaRepository = respostaRepository;
        this.questaoService = questaoService;
        this.applicationEventPublisher = applicationEventPublisher;
        
    }

    @Transactional
    public UUID save(RespostaRequest request, UUID questaoId) {
        
        if (request.getAlternativaId() == null)
            throw new IllegalArgumentException("Alternativa não informada");

        LocalDateTime hoje = LocalDateTime.now();

        boolean acertou = questaoService.isAlternativaCorreta(
            questaoId, request.getAlternativaId());

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        Resposta resposta = new Resposta();
        resposta.setQuestaoId(questaoId);
        resposta.setAlternativaId(request.getAlternativaId());
        resposta.setAcerto(acertou);
        resposta.setData(hoje);
        resposta.setUsuarioId(usuarioLogado.getId());

        respostaRepository.save(resposta);

        PerformanceEvent event = new PerformanceEvent(
            acertou, hoje, usuarioLogado.getId());
        applicationEventPublisher.publishEvent(event);

        return resposta.getId();
    }
}
