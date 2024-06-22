package br.com.foxconcursos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("tarefas_agendadas")
public class TarefaAgendada {
    
    @Id
    private UUID id;
    private LocalDateTime dataExecucao;
    public UUID targetId;
    private TipoTarefaAgendada tipo;
    private StatusTarefaAgendada status;
    @Version
    private int version;

    public TarefaAgendada() {
    }

    public TarefaAgendada(UUID targetId, TipoTarefaAgendada tipo, 
        LocalDateTime dataExecucao) {
        this.targetId = targetId;
        this.tipo = tipo;
        this.dataExecucao = dataExecucao;
        this.status = StatusTarefaAgendada.AGUARDANDO_EXECUCAO;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDateTime dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public TipoTarefaAgendada getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarefaAgendada tipo) {
        this.tipo = tipo;
    }

    public StatusTarefaAgendada getStatus() {
        return status;
    }

    public void setStatus(StatusTarefaAgendada status) {
        this.status = status;
    }
}
