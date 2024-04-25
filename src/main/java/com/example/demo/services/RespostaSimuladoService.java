package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.RespostaSimulado;
import com.example.demo.domain.StatusSimulado;
import com.example.demo.repositories.RespostaSimuladoRepository;

@Service
public class RespostaSimuladoService {
    
    private final RespostaSimuladoRepository respostaSimuladoRepository;

    public RespostaSimuladoService(RespostaSimuladoRepository respostaSimuladoRepository) {
        this.respostaSimuladoRepository = respostaSimuladoRepository;
    }

    public UUID iniciar(UUID simuladoId, UUID usuarioId) {
        
        RespostaSimulado resposta = new RespostaSimulado();
        resposta.setUsuarioId(usuarioId);
        resposta.setSimuladoId(simuladoId);
        resposta.setDataInicio(LocalDateTime.now());
        resposta.setStatus(StatusSimulado.EM_ANDAMENTO);

        resposta = this.respostaSimuladoRepository.save(resposta);

        return resposta.getId();
    }

}
