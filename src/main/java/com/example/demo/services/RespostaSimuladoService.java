package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.RespostaSimulado;
import com.example.demo.domain.RespostaSimuladoQuestao;
import com.example.demo.domain.Simulado;
import com.example.demo.domain.StatusSimulado;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.DisciplinaQuestoesResponse;
import com.example.demo.dto.ItemQuestaoResponse;
import com.example.demo.dto.QuestaoSimuladoResponse;
import com.example.demo.dto.RespostaSimuladoRequest;
import com.example.demo.dto.SimuladoCompletoResponse;
import com.example.demo.repositories.RespostaQuestaoSimuladoRepository;
import com.example.demo.repositories.RespostaSimuladoRepository;
import com.example.demo.services.impl.UsuarioServiceImpl;

@Service
public class RespostaSimuladoService {
    
    private SimuladoService simuladoService;
    private final RespostaSimuladoRepository respostaSimuladoRepository;
    private final RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository;
    private final UsuarioServiceImpl usuarioService;
    private final ItemQuestaoSimuladoService itemQuestaoSimuladoService;

    public RespostaSimuladoService(RespostaSimuladoRepository respostaSimuladoRepository, 
        RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository,
        UsuarioServiceImpl usuarioService, SimuladoService simuladoService, 
        ItemQuestaoSimuladoService itemQuestaoSimuladoService) {
        
        this.usuarioService = usuarioService;
        this.respostaSimuladoRepository = respostaSimuladoRepository;
        this.respostaQuestaoSimuladoRepository = respostaQuestaoSimuladoRepository;
        this.simuladoService = simuladoService;
        this.itemQuestaoSimuladoService = itemQuestaoSimuladoService;

    }

    public UUID iniciar(UUID simuladoId, String login) {
        
        UsuarioLogado user =
            usuarioService.loadUserByUsername(login);

        RespostaSimulado resposta = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());

        if (resposta == null || resposta.getId() == null) {
            resposta = new RespostaSimulado();
            resposta.setUsuarioId(user.getId());
            resposta.setSimuladoId(simuladoId);
            resposta.setDataInicio(LocalDateTime.now());
            resposta.setStatus(StatusSimulado.EM_ANDAMENTO);
            resposta = this.respostaSimuladoRepository.save(resposta);
        }

        return resposta.getId();
    }

    public StatusSimulado obterStatus(UUID simuladoId, String login) {

        UsuarioLogado user = usuarioService.loadUserByUsername(login);

        RespostaSimulado respostaSimulado = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());
        
        if (respostaSimulado == null)
            return StatusSimulado.NAO_INICIADO;
        
        return respostaSimulado.getStatus();
    }

    public UUID salvar(UUID simuladoId, String login, 
        RespostaSimuladoRequest resposta) {
        
        UsuarioLogado user =
            this.usuarioService.loadUserByUsername(login);
        
        UUID respostaSimuladoId =
        this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
            simuladoId, user.getId()).getId();
        
        RespostaSimuladoQuestao respostaDB =
            this.respostaQuestaoSimuladoRepository.findByRespostaSimuladoIdAndQuestaoId(
            respostaSimuladoId, resposta.getQuestaoId());

        Boolean acertou = itemQuestaoSimuladoService.estaCorreta(
            resposta.getItemQuestaoId(), resposta.getQuestaoId());

        if (respostaDB == null) {
            RespostaSimuladoQuestao respostaQuestao =
            new RespostaSimuladoQuestao(
                respostaSimuladoId, resposta.getQuestaoId(), 
                resposta.getItemQuestaoId(), acertou);
            respostaDB = respostaQuestaoSimuladoRepository.save(respostaQuestao);
        } else {
            respostaDB.setCorreta(acertou);
            respostaDB.setItemQuestaoId(resposta.getItemQuestaoId());
            respostaQuestaoSimuladoRepository.save(respostaDB);
        }
        
        return respostaDB.getId();
    }

    @Transactional
    public UUID finalizar(UUID simuladoId, String login, 
        List<RespostaSimuladoRequest> respostas) {
        
        LocalDateTime horaFim = LocalDateTime.now();
        
        Simulado simulado = simuladoService.obterPorId(simuladoId);
        estaFinalizandoAposHorario(simulado.getDataInicio(), 
            simulado.getDuracao(), horaFim);

        UsuarioLogado user =
            usuarioService.loadUserByUsername(login);
        
        RespostaSimulado respostaSimulado =
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
            simuladoId, user.getId());

        int acertos = 0;
        int acertosUltimas15 = 0;

        int quantidadeQuestoes = simulado.getQuantidadeQuestoes();
        int inicio = quantidadeQuestoes - 15;
        int i = 1;
        
        for (RespostaSimuladoRequest resposta : respostas) {

            Boolean acertou = itemQuestaoSimuladoService.estaCorreta(
                resposta.getItemQuestaoId(), resposta.getQuestaoId());
           
            if (acertou) {
                acertos++;
                
                if (i > inicio)
                    acertosUltimas15++;
                
            }           

            RespostaSimuladoQuestao respostaQuestao =
                new RespostaSimuladoQuestao(
                    respostaSimulado.getId(), resposta.getQuestaoId(), 
                    resposta.getItemQuestaoId(), acertou);

            respostaQuestaoSimuladoRepository.save(respostaQuestao);

            i++;
        }
        
        respostaSimulado.setAcertos(acertos);
        respostaSimulado.setAcertosUltimas15(acertosUltimas15);
        respostaSimulado.setDataFim(horaFim);
        respostaSimulado.setStatus(StatusSimulado.FINALIZADO);

        this.respostaSimuladoRepository.save(respostaSimulado);

        return respostaSimulado.getId();
    }

    private void estaFinalizandoAposHorario(
        LocalDateTime dataInicio, String duracao, LocalDateTime horarioEnvio) {
        
        LocalDateTime horarioFim = simuladoService.calcularHoraFim(dataInicio, duracao);
        if (horarioEnvio.isAfter(horarioFim))
            throw new IllegalArgumentException("Simulado finalizado após o horário limite.");
    }

    public SimuladoCompletoResponse obterRespostas(
        SimuladoCompletoResponse simulado, String login) {

        UsuarioLogado usuarioLogado =
            this.usuarioService.loadUserByUsername(login);
        
        RespostaSimulado respostaSimulado = this.respostaSimuladoRepository
            .findBySimuladoIdAndUsuarioId(simulado.getId(), usuarioLogado.getId());
        
        if (respostaSimulado != null) {
            for (DisciplinaQuestoesResponse disciplinas : simulado.getDisciplinas()) {
                preencherQuestoesAluno(disciplinas.getQuestoes(), respostaSimulado.getId());
            }
        }

        return simulado;
    }

    private void preencherQuestoesAluno(List<QuestaoSimuladoResponse> questoes, UUID respostaId) {
        for (QuestaoSimuladoResponse questao : questoes) {
            RespostaSimuladoQuestao resposta = 
                respostaQuestaoSimuladoRepository.findByRespostaSimuladoIdAndQuestaoId(
                    respostaId, questao.getId());
            if (resposta != null)
                preencherItem(questao.getAlternativas(), resposta.getItemQuestaoId());
        }
    }

    private void preencherItem(List<ItemQuestaoResponse> alternativas, UUID id) {
        for (ItemQuestaoResponse alternativa : alternativas) {
            if (id.equals(alternativa.getId())) {
                alternativa.setItemMarcado(true);
                break;
            }
        }
    }
}
