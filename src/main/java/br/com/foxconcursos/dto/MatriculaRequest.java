package br.com.foxconcursos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MatriculaRequest {
    
    private static final String BANCO_QUESTOES_ID = "00000000-0000-0000-0000-000000000000";

    private UUID produtoId; // curso ou simulado
    private UUID usuarioId;
    private LocalDateTime dataFim;
    private BigDecimal valor;

    public MatriculaRequest() {
        
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getDataFim() {
        return this.dataFim;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

    public boolean isBancoQuestao() {
        return this.produtoId != null 
            && this.produtoId.equals(
                UUID.fromString(BANCO_QUESTOES_ID));
    }

}
