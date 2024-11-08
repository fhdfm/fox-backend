package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

import br.com.foxconcursos.domain.Escolaridade;
import br.com.foxconcursos.domain.TipoQuestao;

public class QuestaoRequest {

    private String enunciado;
    private UUID disciplinaId;
    private UUID assuntoId;
    private UUID bancaId;
    private UUID instituicaoId;
    private UUID cargoId;
    private Integer ano;
    private String uf;
    private String cidade;
    private Escolaridade escolaridade;
    private TipoQuestao tipo;
    private String numeroExameOab;
    private UUID escolaMilitarId;
    private String edicao;
    private List<AlternativaRequest> alternativas;

    public QuestaoRequest() {

    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public UUID getAssuntoId() {
        return assuntoId;
    }

    public void setAssuntoId(UUID assuntoId) {
        this.assuntoId = assuntoId;
    }

    public UUID getBancaId() {
        return bancaId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
    }

    public UUID getInstituicaoId() {
        return instituicaoId;
    }

    public void setInstituicaoId(UUID instituicaoId) {
        this.instituicaoId = instituicaoId;
    }

    public UUID getCargoId() {
        return cargoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public void setAlternativas(List<AlternativaRequest> alternativas) {
        this.alternativas = alternativas;
    }

    public List<AlternativaRequest> getAlternativas() {
        return alternativas;
    }

    public TipoQuestao getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoQuestao tipo) {
        this.tipo = tipo;
    }

    public String getNumeroExameOab() {
        return this.numeroExameOab;
    }

    public void setNumeroExameOab(String numeroExameOab) {
        this.numeroExameOab = numeroExameOab;
    }

    public void setEscolaMilitarId(UUID escolaMilitarId) {
        this.escolaMilitarId = escolaMilitarId;
    }

    public UUID getEscolaMilitarId() {
        return this.escolaMilitarId;
    }

    public String getEdicao() {
        return this.edicao;
    }

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

}