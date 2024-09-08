package br.com.foxconcursos.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.domain.TipoArquivo;

public class StorageRequest {

    private MultipartFile file;
    private String folderId;
    private TipoArquivo tipo;
    private UUID disciplinaId;
    private UUID assuntoId;

    public StorageRequest() {
        
    }

    // Getters e Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public TipoArquivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoArquivo tipo) {
        this.tipo = tipo;
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
}
