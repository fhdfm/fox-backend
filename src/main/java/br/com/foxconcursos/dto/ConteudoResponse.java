package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.TipoArquivo;

public class ConteudoResponse {
    
    private UUID id;
    private TipoArquivo tipo;
    private String titulo;
    private String video;
    private String thumbnail;
    private String fileId;

    public ConteudoResponse(UUID id, TipoArquivo tipo, String titulo, String video, String thumbnail, String fileId) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.video = video;
        this.thumbnail = thumbnail;
        this.fileId = fileId;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

}
