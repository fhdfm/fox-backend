package br.com.foxconcursos.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.com.foxconcursos.domain.StatusRecurso;

public class Recurso01Response {
    
    private UUID id;
    private String nome;
    private String fundamentacao;
    private Date dataAbertura;
    private String simulado;
    private UUID questaoId;
    private String questao;
    private List<String> itens;
    private StatusRecurso status;

    public Recurso01Response() {
    }

    public Recurso01Response(String nome, String fundamentacao, 
        Date dataAbertura, String simulado, String questao, 
        List<String> itens, StatusRecurso status, 
        UUID questaoId, UUID id) {
        this.nome = nome;
        this.fundamentacao = fundamentacao;
        this.dataAbertura = dataAbertura;
        this.simulado = simulado;
        this.questao = questao;
        this.itens = itens;
        this.status = status;
        this.questaoId = questaoId;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFundamentacao() {
        return fundamentacao;
    }

    public void setFundamentacao(String fundamentacao) {
        this.fundamentacao = fundamentacao;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getSimulado() {
        return simulado;
    }

    public void setSimulado(String simulado) {
        this.simulado = simulado;
    }

    public String getQuestao() {
        return questao;
    }

    public void setQuestao(String questao) {
        this.questao = questao;
    }

    public List<String> getItens() {
        return itens;
    }

    public void setItens(List<String> itens) {
        this.itens = itens;
    }

    public StatusRecurso getStatus() {
        return status;
    }

    public void setStatus(StatusRecurso status) {
        this.status = status;
    }

    public UUID getQuestaoId() {
        return questaoId;
    }

    public void setQuestaoId(UUID questaoId) {
        this.questaoId = questaoId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
