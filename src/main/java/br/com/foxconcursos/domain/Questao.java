package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.QuestaoRequest;

@Table("questoes")
public class Questao {
    
    @Id
    private UUID id;
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
    private Status status;
    private TipoQuestao tipo;
    private String numeroExameOab;
    private UUID escolaMilitarId;
    private String edicao;
    @Version
    private Integer version;

    public Questao() {
    }

    public Questao(QuestaoRequest request) {
        this.enunciado = request.getEnunciado();
        this.disciplinaId = request.getDisciplinaId();
        this.assuntoId = request.getAssuntoId();
        this.bancaId = request.getBancaId();
        this.instituicaoId = request.getInstituicaoId();
        this.cargoId = request.getCargoId();
        this.ano = request.getAno();
        this.uf = request.getUf();
        this.cidade = request.getCidade();
        this.escolaridade = request.getEscolaridade();
        this.status = Status.ATIVO;
        this.numeroExameOab = request.getNumeroExameOab();
        this.tipo = request.getTipo();
        this.escolaMilitarId = request.getEscolaMilitarId();
        this.edicao = request.getEdicao();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEnunciado() {
        return this.enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public UUID getDisciplinaId() {
        return this.disciplinaId;
    }

    public void setAssuntoId(UUID assuntoId) {
        this.assuntoId = assuntoId;
    }

    public UUID getAssuntoId() {
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

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getAno() {
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

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public void setEdicao(String edicao) {
        this.edicao = edicao;
    }

    public String getEdicao() {
        return this.edicao;
    }

}
