package br.com.foxconcursos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("mercado_pago")
public class Pagamento {

    @Id
    private UUID id;

    @Column("usuario_id")
    private UUID usuarioId;

    @Column("produto_id")
    private UUID produtoId;

    private String mpId;

    private String status;

    private int periodo;

    private TipoProduto tipo;

    private LocalDateTime data;

    @Transient
    private BigDecimal valor;

    public Pagamento() {
    }

    public Pagamento(UUID id, UUID usuarioId, UUID produtoId, String mpId, String status, int periodo, TipoProduto tipo, LocalDateTime data, BigDecimal valor) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.produtoId = produtoId;
        this.mpId = mpId;
        this.status = status;
        this.periodo = periodo;
        this.tipo = tipo;
        this.data = data;
        this.valor = valor;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isAprovado() {
        return "approved".equals(status);
    }
}
