package br.com.foxconcursos.executors.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import br.com.foxconcursos.executors.TarefaExecutor;
import br.com.foxconcursos.services.RecuperarPasswordService;

@Configuration
public class ExcluirSolicitacaoPasswordExecutor implements TarefaExecutor {
    
  private Logger logger = LoggerFactory.getLogger(ExcluirSolicitacaoPasswordExecutor.class);

    private final RecuperarPasswordService recuperarPasswordService;

    public ExcluirSolicitacaoPasswordExecutor(RecuperarPasswordService recuperarPasswordService) {
        this.recuperarPasswordService = recuperarPasswordService;
    }

    @Override
    public void executar(UUID targetId) {
        logger.info("Excluindo solicitacao de recuperacao de senha para o usuario {}", targetId);
        recuperarPasswordService.deleteByUserId(targetId);
        logger.info("Solicitação de recuperação de senha para o usuario {} removida com sucesso.", targetId);
    }
}
