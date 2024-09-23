package br.com.foxconcursos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Resposta;
import br.com.foxconcursos.domain.RespostaFree;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.RespostaRequest;
import br.com.foxconcursos.dto.ResultadoResponse;
import br.com.foxconcursos.events.PerformanceEvent;
import br.com.foxconcursos.repositories.RespostaFreeRepository;
import br.com.foxconcursos.repositories.RespostaRepository;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;
    private final QuestaoService questaoService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RespostaFreeRepository respostaFreeRepository;

    public RespostaService(RespostaRepository respostaRepository, QuestaoService questaoService,
                           ApplicationEventPublisher applicationEventPublisher,
                           RespostaFreeRepository respostaFreeRepository) {

        this.respostaRepository = respostaRepository;
        this.questaoService = questaoService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.respostaFreeRepository = respostaFreeRepository;

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

        UUID disciplinaId = this.questaoService.findDisciplinaIdByQuestaoId(questaoId);

        PerformanceEvent event = new PerformanceEvent(
                resultado.getCorreta(), hoje, usuarioLogado.getId(), disciplinaId);
        applicationEventPublisher.publishEvent(event);

        return resultado;
    }

    public ResultadoResponse salvarDegustacao(RespostaRequest request, UUID questaoId) {
        
        UUID alternativaId = request.getAlternativaId();

        if (alternativaId == null)
            throw new IllegalArgumentException("Alternativa não informada");

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        LocalDate hoje = LocalDate.now();
        int count = this.respostaFreeRepository.countByUsuarioIdAndDataResposta(usuarioId, hoje);
        if (count > 10)
            throw new IllegalStateException("O usuário: " + usuarioLogado.getNome() 
                + " já respondeu 10 questões em: " + FoxUtils.convertLocalDateToDate(hoje));

        Optional<RespostaFree> respostaDB =
                this.respostaFreeRepository.findByUsuarioIdAndQuestaoIdAndAlternativaIdAndDataResposta(
                        usuarioId, questaoId, hoje);
        
        RespostaFree resposta = null;

        if (respostaDB.isPresent()) {
            resposta = respostaDB.get();
            resposta.setAlternativaId(alternativaId);
        } else {
            resposta = new RespostaFree(usuarioId, questaoId, alternativaId, hoje);
        }

        this.respostaFreeRepository.save(resposta);

        return questaoService.isAlternativaCorreta(questaoId, alternativaId);
    }
}
