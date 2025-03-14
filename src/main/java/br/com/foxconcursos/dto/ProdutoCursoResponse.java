package br.com.foxconcursos.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Escolaridade;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.util.FoxUtils;

public class ProdutoCursoResponse extends ProdutoResponse {
    
    private String titulo;
    private String banca;
    private Escolaridade escolaridade;
    private Date dataInicio;
    private Date dataTermino;
    private String imagem;
    private Status status;
    private BigDecimal valor;
    private List<ProdutoSimuladoResponse> simulados; 
    private ProdutoBancoQuestaoResponse bancoQuestao;

    public ProdutoCursoResponse() {
    }

    public ProdutoCursoResponse(Curso curso, String banca) {
        setId(curso.getId());
        setTipoProduto(TipoProduto.CURSO);
        this.titulo = curso.getTitulo();
        this.banca = banca;
        this.escolaridade = curso.getEscolaridade();
        if (curso.getDataInicio() != null)
            this.dataInicio = FoxUtils.convertLocalDateToDate(curso.getDataInicio());
        if (curso.getDataTermino() != null)
            this.dataTermino = FoxUtils.convertLocalDateToDate(curso.getDataTermino());
        this.status = curso.getStatus();
        this.valor = curso.getValor();
        this.imagem = curso.getImagem();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBanca() {
        return banca;
    }

    public void setBanca(String banca) {
        this.banca = banca;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setSimulados(List<ProdutoSimuladoResponse> simulados) {
        this.simulados = simulados;
    }

    public List<ProdutoSimuladoResponse> getSimulados() {
        return this.simulados;
    }

    public ProdutoBancoQuestaoResponse getBancoQuestao() {
        return this.bancoQuestao;
    }

    public void setBancoQuestao(ProdutoBancoQuestaoResponse bq) {
        this.bancoQuestao = bq;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
