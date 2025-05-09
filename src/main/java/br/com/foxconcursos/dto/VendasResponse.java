package br.com.foxconcursos.dto;

import java.util.Date;
import java.util.UUID;

public class VendasResponse {
    
    private UUID id;
    private UUID usuarioId;
    private String usuarioNome;
    private UUID produtoId;
    private String produtoNome;
    private String mercadoPagoId;
    private String data;
    private Date dataExpiracao;
    private boolean entrega;
    private boolean enviado;
    private String telefone;
    private String tipo;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    

    public VendasResponse() {
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

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public String getMercadoPagoId() {
        return mercadoPagoId;
    }

    public void setMercadoPagoId(String mercadoPagoId) {
        this.mercadoPagoId = mercadoPagoId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public boolean isEntrega() {
        return entrega;
    }

    public void setEntrega(boolean entrega) {
        this.entrega = entrega;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLogradouro() {
        return logradouro;
    }
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

}
