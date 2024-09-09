package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("storage")
public class Storage {

    @Id
    private UUID id;  // Identificador único
    private String url;  // URL
    private TipoArquivo tipo;  // Tipo (máximo 10 caracteres)
    private UUID disciplinaId;  // FK com a tabela disciplinas
    private UUID assuntoId;  // FK com a tabela assunto
    private String folder;
    private String thumbnail;
    @Version
    private int version;  // Versão, valor padrão 0

    // Construtor sem argumentos
    public Storage() {
    }

    // Construtor com argumentos
    public Storage(UUID id, String url, TipoArquivo tipo, 
        UUID disciplinaId, UUID assuntoId, String folder, String thumbnail) {
        this.id = id;
        this.url = url;
        this.tipo = tipo;
        this.disciplinaId = disciplinaId;
        this.assuntoId = assuntoId;
        this.folder = folder;
        this.thumbnail = thumbnail;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getVersion() {
        return version;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
