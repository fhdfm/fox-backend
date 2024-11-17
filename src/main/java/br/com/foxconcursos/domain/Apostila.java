package br.com.foxconcursos.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import br.com.foxconcursos.dto.ApostilaRequest;
import br.com.foxconcursos.dto.ApostilaResponse;

@Table("apostilas")
public class Apostila {

    @Id
    private UUID id;
    private String nome;
    private String descricao;
    private String imagem;
    private BigDecimal valor;
    private Status status;
    private String cargo;
    private String cidade;
    private String uf;
    @Version
    private int version;

    public Apostila() {

    }

    public Apostila(UUID id, String nome, String descricao, String imagem, BigDecimal valor, Status status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.valor = valor;
        this.status = status;
    }

    public Apostila(String nome, String descricao, String imagem, BigDecimal valor, Status status, String cargo, String cidade, String uf) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.valor = valor;
        this.status = status;
        this.cargo = cargo;
        this.cidade = cidade;
        this.uf = uf;
    }

    public ApostilaResponse toAssembly() {
        return new ApostilaResponse(id, nome, descricao, imagem, valor, status, cargo, cidade, uf);
    }

    public void updateFromRequest(ApostilaRequest request) {
        this.setNome(request.getNome());
        this.setDescricao(request.getDescricao());
        this.setImagem(request.getImagem());
        this.setValor(request.getValor());
        this.setStatus(request.getStatus());
        this.setUf(request.getUf());
        this.setCidade(request.getCidade());
        this.setCargo(request.getCargo());
    }

    public void updateFromRequest(UUID id, ApostilaRequest request) {
        this.id = id;
        updateFromRequest(request);
    }

    // Getters e setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCargo() {
        return this.cargo;
    }

    @Override
    public String toString() {
        return "Apostila{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", imagem='" + imagem + '\'' +
                ", valor=" + valor +
                ", status='" + status +
                '}';
    }
}
