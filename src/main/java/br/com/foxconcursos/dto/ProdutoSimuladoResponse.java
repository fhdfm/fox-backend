package br.com.foxconcursos.dto;

import java.math.BigDecimal;
import java.util.Date;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.util.FoxUtils;

public class ProdutoSimuladoResponse extends ProdutoResponse {
    
    private String titulo;
    private Date data;
    private Integer quantidadeQuestoes;
    private String duracao;
    private BigDecimal valor;

    public ProdutoSimuladoResponse() {
    }

    public ProdutoSimuladoResponse(Simulado simulado) {
        setId(simulado.getId());
        setTipoProduto(TipoProduto.SIMULADO);
        this.titulo = simulado.getTitulo();
        this.data = FoxUtils.convertLocalDateTimeToDate(simulado.getDataInicio());
        this.quantidadeQuestoes = simulado.getQuantidadeQuestoes();
        this.duracao = simulado.getDuracao();
        this.valor = simulado.getValor();
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getData() {
        return data;
    }

    public void setQuantidadeQuestoes(Integer quantidadeQuestoes) {
        this.quantidadeQuestoes = quantidadeQuestoes;
    }

    public Integer getQuantidadeQuestoes() {
        return quantidadeQuestoes;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
