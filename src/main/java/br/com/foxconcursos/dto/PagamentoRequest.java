package br.com.foxconcursos.dto;

import java.math.BigDecimal;

public class PagamentoRequest {
    
    private String token;
    private BigDecimal transactionAmount;
    private String description;
    private int installments;
    private String paymentMethodId;
    private String issuerId;
    private String email;

    public PagamentoRequest() {
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInstallments() {
        return this.installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public String getPaymentMethodId() {
        return this.paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getIssuerId() {
        return this.issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
