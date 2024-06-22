package br.com.foxconcursos.executors.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.executors.TarefaExecutor;
import br.com.foxconcursos.services.RespostaSimuladoService;

@Component
public class FinalizarTarefaExecutor implements TarefaExecutor {
    
    private Logger logger = LoggerFactory.getLogger(FinalizarTarefaExecutor.class);

    private final RespostaSimuladoService respostaSimuladoService;

    public FinalizarTarefaExecutor(RespostaSimuladoService respostaSimuladoService) {
        this.respostaSimuladoService = respostaSimuladoService;
    }

    @Override
    public void executar(UUID targetId) {
        logger.info("Finalizando simulado {}", targetId);
        respostaSimuladoService.finalizarSimulado(targetId);
        logger.info("Simulado {} finalizado", targetId);
    }

    
}
