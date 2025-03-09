package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.TipoArquivo;

public class ConteudoResponse {

    private UUID id;
    private UUID aulaId;
    private TipoArquivo tipo;
    private String titulo;
    private String vimeo;
    private String url;
    private String key;
    private String mimetype;

    // Construtor padrão
    public ConteudoResponse() {}

    // Construtor com campos principais
    public ConteudoResponse(UUID id, UUID aulaId, TipoArquivo tipo, String titulo, String url, String key, String mimetype,String vimeo) {
        this.id = id;
        this.aulaId = aulaId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.url = url;
        this.key = key;
        this.vimeo = vimeo;
        this.mimetype = mimetype;
    }

    public ConteudoResponse(TipoArquivo tipo, String titulo) {
        this.tipo = tipo;
        this.titulo = titulo;
    }

    // Método para conversão para DTO (ConteudoResponse)
    // public ConteudoResponse toAssembly() {
    //     return new ConteudoResponse(aulaId, tipo, titulo, url, key, mimetype);
    // }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAulaId() {
        return aulaId;
    }

    public void setAulaId(UUID aulaId) {
        this.aulaId = aulaId;
    }

    public TipoArquivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoArquivo tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getVimeo() {
        return vimeo;
    }

    public void setVimeo(String vimeo) {
        this.vimeo = vimeo;
    }
}
