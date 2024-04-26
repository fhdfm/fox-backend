package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.RespostaSimulado;
import com.example.demo.domain.StatusSimulado;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.repositories.RespostaSimuladoRepository;
import com.example.demo.services.impl.UsuarioServiceImpl;

@Service
public class RespostaSimuladoService {
    
    private final RespostaSimuladoRepository respostaSimuladoRepository;
    private final UsuarioServiceImpl usuarioService;

    public RespostaSimuladoService(RespostaSimuladoRepository respostaSimuladoRepository, 
        UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
        this.respostaSimuladoRepository = respostaSimuladoRepository;
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

}
