package br.com.foxconcursos.domain;

import java.util.List;
import java.util.UUID;


public class FiltroQuestao {
    
    private String enunciado;
    private List<UUID> disciplinaId;
    private List<UUID> assuntoId;
    private UUID bancaId;
    private UUID instituicaoId;
    private UUID cargoId;
    private String ano;
    private String uf;
    private String cidade;
    private Escolaridade escolaridade;
    private String numeroExameOab;
    private String tipoProvaEnem;
    private TipoQuestao tipo;
    private UUID escolaMilitarId;
    private Integer periodo;
    private boolean comentarios = false;
    
    public FiltroQuestao() {
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public List<UUID> getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(List<UUID> disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public List<UUID> getAssuntoId() {
        return assuntoId;
    }

    public void setAssuntoId(List<UUID> assuntoId) {
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

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
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

    public String getNumeroExameOab() {
        return numeroExameOab;
    }

    public void setNumeroExameOab(String numeroExameOab) {
        this.numeroExameOab = numeroExameOab;
    }

    public String getTipoProvaEnem() {
        return tipoProvaEnem;
    }

    public void setTipoProvaEnem(String tipoProvaEnem) {
        this.tipoProvaEnem = tipoProvaEnem;
    }

    public TipoQuestao getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuestao tipo) {
        this.tipo = tipo;
    }

    public UUID getEscolaMilitarId() {
        return escolaMilitarId;
    }

    public void setEscolaMilitarId(UUID escolaMilitarId) {
        this.escolaMilitarId = escolaMilitarId;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public boolean getComentarios() {
        return comentarios;
    }

    public void setComentarios(boolean comentarios) {
        this.comentarios = comentarios;
    }
}
