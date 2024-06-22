package br.com.foxconcursos.executors.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.executors.TarefaExecutor;
import br.com.foxconcursos.services.SimuladoService;

@Component
public class EsquentarCacheExecutor implements TarefaExecutor {

    private Logger logger = LoggerFactory.getLogger(EsquentarCacheExecutor.class);

    private final SimuladoService simuladoService;

    public EsquentarCacheExecutor(SimuladoService simuladoService) {
        this.simuladoService = simuladoService;
    }

    @Override
    public void executar(UUID targetId) {
        logger.info("Esquentando cache do simulado {}", targetId);
        simuladoService.prepararSimulado(targetId); // esquentar cache...
        logger.info("Cache do simulado {} esquentado", targetId);
    }
    
}
