package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("performance")
public class Performance {
    
    @Id
    private UUID id;
    private UUID usuarioId;
    private int mes;
    private int ano;
    private int acertos;
    private int erros;
    @Version
    private int version;

    public Performance() {
    }

    public Performance(UUID usuarioId, int mes, int ano, int acertos, int erros) {
        this.usuarioId = usuarioId;
        this.mes = mes;
        this.ano = ano;
        this.acertos = acertos;
        this.erros = erros;
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

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getAcertos() {
        return acertos;
    }

    public void setAcertos(int acertos) {
        this.acertos = acertos;
    }

    public int getErros() {
        return erros;
    }

    public void setErros(int erros) {
        this.erros = erros;
    }
}
