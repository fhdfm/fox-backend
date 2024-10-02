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
    private TipoQuestao tipo;
    
    public FiltroQuestao() {
    }
    
    public String getEnunciado() {
        return this.enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setDisciplinaId(List<UUID> disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public List<UUID> getDisciplinaId() {
        return this.disciplinaId;
    }

    public void setAssuntoId(List<UUID> assuntoId) {
        this.assuntoId = assuntoId;
    }

    public List<UUID> getAssuntoId() {
        return this.assuntoId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
    }

    public UUID getBancaId() {
        return this.bancaId;
    }

    public void setInstituicaoId(UUID instituicaoId) {
        this.instituicaoId = instituicaoId;
    }

    public UUID getInstituicaoId() {
        return this.instituicaoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }

    public UUID getCargoId() {
        return this.cargoId;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getAno() {
        return this.ano;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUf() {
        return this.uf;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCidade() {
        return this.cidade;
    }

    public Escolaridade getEscolaridade() {
        return this.escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }
    
    public String getNumeroExameOab() {
        return this.numeroExameOab;
    }

    public void setNumeroExameOab(String numeroExameOab) {
        this.numeroExameOab = numeroExameOab;
    }

    public TipoQuestao getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoQuestao tipo) {
        this.tipo = tipo;
    }

}
