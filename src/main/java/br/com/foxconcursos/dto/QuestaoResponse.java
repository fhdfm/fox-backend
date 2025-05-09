package br.com.foxconcursos.dto;

import java.util.List;
import java.util.UUID;

public class QuestaoResponse {

    private UUID id;
    private String enunciado;
    private String escolaridade;
    private String uf;
    private String cidade;
    private int ano;
    private UUID bancaId;
    private String banca;
    private UUID instituicaoId;
    private String instituicao;
    private UUID disciplinaId;
    private String disciplina;
    private UUID cargoId;
    private String cargo;
    private UUID assuntoId;
    private String assunto;
    private List<AssuntoResponse> assuntos;
    private String acerto;
    private List<AlternativaResponse> alternativas;
    private int comentarios;
    private UUID alternativaSelecionadaId;
    private String numeroExameOab;
    private UUID escolaMilitarId;
    private String escolaMilitar;
    private String tipoProvaEnem;
    private Integer periodo;
    private QuestaoVideoResponse video;
    private boolean possuiVideo;
    private boolean comentada;

    public QuestaoResponse() {  }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public UUID getBancaId() {
        return bancaId;
    }

    public void setBancaId(UUID bancaId) {
        this.bancaId = bancaId;
    }

    public String getBanca() {
        return banca;
    }

    public void setBanca(String banca) {
        this.banca = banca;
    }

    public UUID getInstituicaoId() {
        return instituicaoId;
    }

    public void setInstituicaoId(UUID instituicaoId) {
        this.instituicaoId = instituicaoId;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public UUID getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(UUID disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public UUID getCargoId() {
        return cargoId;
    }

    public void setCargoId(UUID cargoId) {
        this.cargoId = cargoId;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public UUID getAssuntoId() {
        return assuntoId;
    }

    public void setAssuntoId(UUID assuntoId) {
        this.assuntoId = assuntoId;
    }

    public String getAssunto() {
        if (assuntos != null && !assuntos.isEmpty()) {
            AssuntoResponse assunto = assuntos.get(0);
            return assunto != null ? assunto.getId().toString() : "";
        }        
        return "";
    }

    public String getAssuntoStr() {
        if (assuntos != null && !assuntos.isEmpty()) {
            AssuntoResponse assunto = assuntos.get(0);
            return assunto != null ? assunto.getNome() : "";
        }
        return "";
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public List<AlternativaResponse> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<AlternativaResponse> alternativas) {
        this.alternativas = alternativas;
    }

    public void setAcerto(String acerto) {
        this.acerto = acerto;
    }

    public String getAcerto() {
        return this.acerto;
    }

    public int getComentarios() {
        return this.comentarios;
    }

    public void setComentarios(int qtd) {
        this.comentarios = qtd;
    }

    public UUID getAlternativaSelecionadaId() {
        return alternativaSelecionadaId;
    }

    public void setAlternativaSelecionadaId(UUID alternativaSelecionadaId) {
        this.alternativaSelecionadaId = alternativaSelecionadaId;
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

    public void setEscolaMilitar(String escolaMilitar) {
        this.escolaMilitar = escolaMilitar;
    }

    public String getEscolaMilitar() {
        return this.escolaMilitar;
    }

    public String getTipoProvaEnem() {
        return tipoProvaEnem;
    }

    public void setTipoProvaEnem(String tipoProvaEnem) {
        this.tipoProvaEnem = tipoProvaEnem;
    }

    public void setAssuntos(List<AssuntoResponse> assuntos) {
        this.assuntos = assuntos;
    }

    public List<AssuntoResponse> getAssuntos() {
        return this.assuntos;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getPeriodo() {
        return this.periodo;
    }

    public void setVideo(QuestaoVideoResponse video) {
        this.video = video;
    }

    public QuestaoVideoResponse getVideo() {
        return this.video;
    }

    public boolean getPossuiVideo() {
        return this.possuiVideo;
    }

    public void setPossuiVideo(boolean possuiVideo) {
        this.possuiVideo = possuiVideo;
    }

    public boolean isComentada() {
        return comentada;
    }

    public void setComentada(boolean comentada) {
        this.comentada = comentada;
    }

}
