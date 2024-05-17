package  br.com.foxconcursos.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("respostas_simulado")
public class RespostaSimulado {

    @Id
    private UUID id;
    private UUID simuladoId;
    private UUID usuarioId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer acertos;
    @Column("acertos_ultimas_15")
    private Integer acertosUltimas15;
    private StatusSimulado status;
    
    public RespostaSimulado() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSimuladoId() {
        return this.simuladoId;
    }

    public void setSimuladoId(UUID simuladoId) {
        this.simuladoId = simuladoId;
    }

    public UUID getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataInicio() {
        return this.dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return this.dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getAcertos() {
        return this.acertos;
    }

    public void setAcertos(Integer acertos) {
        this.acertos = acertos;
    }

    public Integer getAcertosUltimas15() {
        return this.acertosUltimas15;
    }

    public void setAcertosUltimas15(Integer acertosUltimas15) {
        this.acertosUltimas15 = acertosUltimas15;
    }

    public StatusSimulado getStatus() {
        return this.status;
    }

    public void setStatus(StatusSimulado status) {
        this.status = status;
    }

}