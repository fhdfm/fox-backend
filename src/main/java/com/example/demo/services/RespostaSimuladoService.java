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
import com.example.demo.dto.RespostaSimuladoRequest;
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

        RespostaSimulado resposta = new RespostaSimulado();
        resposta.setUsuarioId(user.getId());
        resposta.setSimuladoId(simuladoId);
        resposta.setDataInicio(LocalDateTime.now());
        resposta.setStatus(StatusSimulado.EM_ANDAMENTO);

        resposta = this.respostaSimuladoRepository.save(resposta);

        return resposta.getId();
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
        
        int acertos = 0;
        int acertosUltimas15 = 0;

        int quantidadeQuestoes = simulado.getQuantidadeQuestoes();
        int inicio = quantidadeQuestoes - 15;
        int i = 1;
        
        for (RespostaSimuladoRequest resposta : respostas) {

            Boolean acertou = itemQuestaoSimuladoService.estaCorreta(
                simuladoId, simuladoId);
           
            if (acertou) {
                acertos++;
                
                if (i > inicio)
                    acertosUltimas15++;
                
            }           

            RespostaSimuladoQuestao respostaQuestao =
                new RespostaSimuladoQuestao(simuladoId, resposta.getQuestaoId(), 
                    resposta.getItemQuestaoId(), acertou);

            respostaQuestaoSimuladoRepository.save(respostaQuestao);

            i++;
        }
        
        RespostaSimulado resposta =
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());
        
        resposta.setAcertos(acertos);
        resposta.setAcertosUltimas15(acertosUltimas15);
        resposta.setDataFim(horaFim);
        resposta.setStatus(StatusSimulado.FINALIZADO);

        this.respostaSimuladoRepository.save(resposta);

        return resposta.getId();
    }



    private void estaFinalizandoAposHorario(
        LocalDateTime dataInicio, String duracao, LocalDateTime horarioEnvio) {
        
        LocalDateTime horarioFim = simuladoService.calcularHoraFim(dataInicio, duracao);
        if (horarioEnvio.isAfter(horarioFim))
            throw new IllegalArgumentException("Simulado finalizado após o horário limite.");
    }
}
