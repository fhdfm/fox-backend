package br.com.foxconcursos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Matricula;
import br.com.foxconcursos.domain.Resposta;
import br.com.foxconcursos.domain.RespostaFree;
import br.com.foxconcursos.domain.TipoProduto;
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
    private final MatriculaService matriculaService;

    public RespostaService(RespostaRepository respostaRepository, QuestaoService questaoService,
                           ApplicationEventPublisher applicationEventPublisher,
                           RespostaFreeRepository respostaFreeRepository, 
                           MatriculaService matriculaService) {

        this.respostaRepository = respostaRepository;
        this.questaoService = questaoService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.respostaFreeRepository = respostaFreeRepository;
        this.matriculaService = matriculaService;

    }

    @Transactional
    private ResultadoResponse save(RespostaRequest request, UUID questaoId, UUID usuarioId) {

        if (request.getAlternativaId() == null)
            throw new IllegalArgumentException("Alternativa não informada");

        LocalDateTime hoje = LocalDateTime.now();

        ResultadoResponse resultado = questaoService.isAlternativaCorreta(
                questaoId, request.getAlternativaId());

        Resposta resposta = respostaRepository.findByQuestaoIdAndUsuarioId(questaoId, usuarioId);

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
            resposta.setUsuarioId(usuarioId);
        }

        respostaRepository.save(resposta);

        UUID disciplinaId = this.questaoService.findDisciplinaIdByQuestaoId(questaoId);

        PerformanceEvent event = new PerformanceEvent(
                resultado.getCorreta(), hoje, usuarioId, disciplinaId);
        applicationEventPublisher.publishEvent(event);

        return resultado;
    }

    private ResultadoResponse saveDesgustacao(RespostaRequest request, UUID questaoId, UUID usuarioId) {
        
        UUID alternativaId = request.getAlternativaId();

        if (alternativaId == null)
            throw new IllegalArgumentException("Alternativa não informada");

        LocalDate hoje = LocalDate.now();
        int count = this.respostaFreeRepository.countByUsuarioIdAndDataResposta(usuarioId, hoje);
        if (count > 10)
            throw new IllegalStateException("O usuário "  
                + "já respondeu 10 questões em: " + FoxUtils.convertLocalDateToDate(hoje));

        Optional<RespostaFree> respostaDB =
                this.respostaFreeRepository.findByUsuarioIdAndQuestaoIdAndAlternativaIdAndDataResposta(
                        usuarioId, questaoId, alternativaId, hoje);
        
        RespostaFree resposta = null;

        if (respostaDB.isPresent()) {
            resposta = respostaDB.get();
            resposta.setAlternativaId(alternativaId);
        } else {
            resposta = new RespostaFree(usuarioId, questaoId, alternativaId, hoje);
            count++;
        }

        this.respostaFreeRepository.save(resposta);

        ResultadoResponse result = questaoService.isAlternativaCorreta(questaoId, alternativaId);
        result.setQtdRespondidas(count);

        return result;
    }

    public ResultadoResponse create(RespostaRequest request, UUID questaoId) {
        
        UsuarioLogado currentUser = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = currentUser.getId();
        List<Matricula> matriculas = this.matriculaService.findByUsuarioId(usuarioId);
        
        if (matriculas != null && !matriculas.isEmpty())
            for (Matricula matricula : matriculas) {
                if (matricula.getTipoProduto() == TipoProduto.CURSO 
                    || matricula.getTipoProduto() == TipoProduto.QUESTOES)
                    return this.save(request, questaoId, usuarioId);
            }    
        
        return this.saveDesgustacao(request, questaoId, usuarioId);
    }
}
