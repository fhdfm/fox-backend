package br.com.foxconcursos.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.TarefaAgendada;
import br.com.foxconcursos.domain.TipoTarefaAgendada;
import br.com.foxconcursos.events.SimuladoEvent;
import br.com.foxconcursos.events.TokenEvent;
import br.com.foxconcursos.executors.TarefaExecutor;
import br.com.foxconcursos.executors.impl.EsquentarCacheExecutor;
import br.com.foxconcursos.executors.impl.ExcluirSolicitacaoPasswordExecutor;
import br.com.foxconcursos.executors.impl.FinalizarTarefaExecutor;
import br.com.foxconcursos.repositories.TarefaAgendadaRepository;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class TarefaAgendadaService {
    
    private Logger logger = LoggerFactory.getLogger(TarefaAgendadaService.class);

    private final TarefaAgendadaRepository tarefaAgendadaRepository;
    private final TaskScheduler taskScheduler;
    private final EsquentarCacheExecutor esquentarCacheExecutor;
    private final FinalizarTarefaExecutor finalizarSimuladoExecutor;
    private final ExcluirSolicitacaoPasswordExecutor excluirSolicitacaoPasswordExecutor;

    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> tarefasAgendadas = new ConcurrentHashMap<>();
    private Map<TipoTarefaAgendada, TarefaExecutor> executors;

    public TarefaAgendadaService(
        TarefaAgendadaRepository tarefaAgendadaRepository, 
        EsquentarCacheExecutor esquentarCacheExecutor,
        FinalizarTarefaExecutor finalizarSimuladoExecutor,
        ExcluirSolicitacaoPasswordExecutor excluirSolicitacaoPasswordExecutor,
        TaskScheduler taskScheduler) {
        
        this.tarefaAgendadaRepository = tarefaAgendadaRepository;
        this.esquentarCacheExecutor = esquentarCacheExecutor;
        this.finalizarSimuladoExecutor = finalizarSimuladoExecutor;
        this.taskScheduler = taskScheduler;
        this.excluirSolicitacaoPasswordExecutor = excluirSolicitacaoPasswordExecutor;

    }

    @PostConstruct
    public void init() {
        logger.info("Iniciando serviço de tarefas agendadas...");
        
        this.executors = new EnumMap<>(TipoTarefaAgendada.class);
        this.executors.put(TipoTarefaAgendada.SIMULADO_ESQUENTAR_CACHE, esquentarCacheExecutor);
        this.executors.put(TipoTarefaAgendada.SIMULADO_FINALIZAR_APOS_LIMITE, finalizarSimuladoExecutor);
        this.executors.put(TipoTarefaAgendada.EXCLUIR_SOLICITACAO_PASSWORD, excluirSolicitacaoPasswordExecutor);

        logger.info("Iniciado com sucesso...");

        reagendarTarefas();
    }

    public TarefaExecutor getExecutor(TipoTarefaAgendada tipo) {
        return executors.get(tipo);
    }

    public void reagendarTarefas() {
        logger.info("Reagendando tarefas...");

        List<TarefaAgendada> tarefas = tarefaAgendadaRepository.carregarTarefasParaAgendamento();
        for (TarefaAgendada tarefa : tarefas) {
            if (tarefa.getDataExecucao().isAfter(LocalDateTime.now())) {
                agendarTarefa(tarefa);
                logger.info("[targetId: " + tarefa.getTargetId() + "] | Tarefa reagendada: " + tarefa.getTipo().name() + " - " + tarefa.getDataExecucao());
            }
        }

        logger.info("Tarefas reagendadas com sucesso...");
    }

    public void agendarTarefa(TarefaAgendada tarefa) {
        LocalDateTime dataExecucao = tarefa.getDataExecucao();
        Runnable task = () -> {
            executarTarefa(tarefa);
            removerTarefa(tarefa.getId());
        };

        Instant dataInicio = dataExecucao.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> future = taskScheduler.schedule(task, dataInicio);
        tarefasAgendadas.put(tarefa.getId(), future);
    }

    public void executarTarefa(TarefaAgendada tarefa) {
        
        TarefaExecutor executor = getExecutor(tarefa.getTipo());

        logger.info("Executando tarefa agendada...");
        logger.info("[targetId: " + tarefa.getTargetId() + "] | Tarefa a executar: " + tarefa.getTipo().name() + " - " + tarefa.getDataExecucao());

        if (executor != null) {
            executor.executar(tarefa.getTargetId());
            logger.info("Tarefa executada com sucesso...");
        } else {
            logger.error("Executor não encontrado para a tarefa: " + tarefa);
            throw new IllegalArgumentException("Executor não encontrado para a tarefa: " + tarefa);
        }
    }

    public void removerTarefa(UUID tarefaId) {
        logger.info("Removendo tarefa agendada: " + tarefaId);

        ScheduledFuture<?> future = tarefasAgendadas.remove(tarefaId);

        if (future != null) {
            future.cancel(false);
            tarefasAgendadas.remove(tarefaId);
        }

        this.tarefaAgendadaRepository.deleteById(tarefaId);

        logger.info("Tarefa agendada: " + tarefaId + " removida com sucesso...");
    }

    public void save(List<TarefaAgendada> tarefas) {
        logger.info("[save] salvar tarefas agendadas...");

        boolean reagendar = false;

        for (TarefaAgendada tarefa : tarefas) {
            TarefaAgendada tarefaExistente = tarefaAgendadaRepository.findByTargetIdAndTipo(
                tarefa.getTargetId(), tarefa.getTipo()).orElse(null);

            if (tarefaExistente == null) {
                logger.info("[save|insert] - begin");
                logger.info("[targetId: " + tarefa.getTargetId() + "] | Tarefa a agendar: " 
                    + tarefa.getTipo().name() + " - " + tarefa.getDataExecucao());

                tarefaAgendadaRepository.save(tarefa);
                agendarTarefa(tarefa);

                logger.info("[save|insert] - end");
            } else {
                logger.info("[save|update] - begin");

                boolean ehDiferente = !(tarefaExistente.getDataExecucao().isEqual(tarefa.getDataExecucao()));
                logger.info("!(tarefaExistente.getDataExecucao().isEqual(tarefa.getDataExecucao())) is: " + ehDiferente);

                if (ehDiferente) {
                    tarefaExistente.setDataExecucao(tarefa.getDataExecucao());
                    tarefaAgendadaRepository.save(tarefaExistente);
                    reagendar = true;
                    logger.info("[save|update] - end");
                }
            }
        }

        if (reagendar) {
            reagendarTarefas();
        }

        logger.info("[save] fim...");
    }

    @EventListener
    public void handleSimuladoEvent(SimuladoEvent event) {
        logger.info("Evento de simulado recebido...");

        Simulado simulado = event.getSimulado();
        logger.info("Simulado: " + simulado.getId());

        List<TarefaAgendada> tarefas = new ArrayList<>();

        LocalDateTime dataInicio = LocalDateTime.from(simulado.getDataInicio().atZone(ZoneId.systemDefault()));

        tarefas.add(new TarefaAgendada(simulado.getId(), TipoTarefaAgendada.SIMULADO_ESQUENTAR_CACHE, dataInicio.minusMinutes(5)));

        LocalDateTime dataFim = FoxUtils.calcularHoraFimSimulado(simulado.getDataInicio(), simulado.getDuracao());

        tarefas.add(new TarefaAgendada(simulado.getId(), TipoTarefaAgendada.SIMULADO_FINALIZAR_APOS_LIMITE, dataFim.plusMinutes(5)));

        save(tarefas);

        logger.info("Evento processado com sucesso...");
    }

    @EventListener
    public void handleTokenEvent(TokenEvent event) {
        
        logger.info("Evento de token recebido...");

        if (event.isAdd()) {

            logger.info("ADD token para usuario: " + event.getUsuarioId());

            List<TarefaAgendada> tarefas = new ArrayList<>();

            tarefas.add(new TarefaAgendada(event.getUsuarioId(), 
                TipoTarefaAgendada.EXCLUIR_SOLICITACAO_PASSWORD, 
                event.getDataExpiracao().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    
            save(tarefas);

            logger.info("Tarefa adicionada com sucesso...");
        } else if (event.isRemove()) {
            
            logger.info("REMOVE token para usuario: " + event.getUsuarioId());

            TarefaAgendada tarefa = tarefaAgendadaRepository.findByTargetIdAndTipo(
                event.getUsuarioId(), TipoTarefaAgendada.EXCLUIR_SOLICITACAO_PASSWORD).orElse(null);

            if (tarefa != null) {
                removerTarefa(tarefa.getId());
            }

            logger.info("Tarefa removida com sucesso...");
        }

        logger.info("Evento processado com sucesso...");
    }
}
