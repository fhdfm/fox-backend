package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Resposta;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.RespostaRequest;
import br.com.foxconcursos.dto.ResultadoResponse;
import br.com.foxconcursos.events.PerformanceEvent;
import br.com.foxconcursos.repositories.RespostaRepository;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public ResultadoResponse save(RespostaRequest request, UUID questaoId) {

        if (request.getAlternativaId() == null)
            throw new IllegalArgumentException("Alternativa não informada");

        LocalDateTime hoje = LocalDateTime.now();

        ResultadoResponse resultado = questaoService.isAlternativaCorreta(
                questaoId, request.getAlternativaId());

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        Resposta resposta = respostaRepository.findByQuestaoIdAndUsuarioId(questaoId, usuarioLogado.getId());

        if (resposta != null) {
            resposta.setAlternativaId(request.getAlternativaId());
            resposta.setAcerto(resultado.getCorreta());
            resposta.setData(hoje);
        } else {
            resposta = new Resposta();

            resposta.setQuestaoId(questaoId);
            resposta.setAlternativaId(request.getAlternativaId());
            resposta.setAcerto(resultado.getCorreta());
            resposta.setData(hoje);
            resposta.setUsuarioId(usuarioLogado.getId());
        }

        respostaRepository.save(resposta);

        PerformanceEvent event = new PerformanceEvent(
                resultado.getCorreta(), hoje, usuarioLogado.getId());
        applicationEventPublisher.publishEvent(event);

        return resultado;
    }
}
