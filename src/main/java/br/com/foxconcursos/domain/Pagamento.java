package br.com.foxconcursos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidade que representa a tabela "mercado_pago".
 */
@Table("mercado_pago")  // Nome da tabela no banco
public class Pagamento {

    @Id
    private UUID id;  // PK com UUID

    // Por padrão, Spring Data JDBC deriva o nome da coluna de "usuarioId" -> "usuario_id"
    // Mas se quiser garantir explicitamente:
    @Column("usuario_id")
    private UUID usuarioId;

    @Column("produto_id")
    private UUID produtoId;

    // Pode usar @Column("mp_id") para garantir. Se não usar, Spring Data JDBC converte mpId -> mp_id
    private String mpId;

    private String status;

    // Mapeado como TIMESTAMP (ou TIMESTAMPTZ) no banco
    private LocalDateTime data;

    @Transient
    private BigDecimal valor;

    // Construtor padrão (obrigatório para frameworks)
    public Pagamento() {
    }

    // (Opcional) Construtor completo para facilitar criação de objetos
    public Pagamento(UUID id, UUID usuarioId, UUID produtoId, String mpId, String status, LocalDateTime data) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.produtoId = produtoId;
        this.mpId = mpId;
        this.status = status;
        this.data = data;
    }

    // Getters e Setters (ou use Lombok @Data, se preferir)

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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public boolean isAprovado() {
        return "approved".equals(status);
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

}
