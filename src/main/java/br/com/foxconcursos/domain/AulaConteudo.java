package br.com.foxconcursos.domain;

import br.com.foxconcursos.dto.ConteudoResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("aula_conteudo")
public class AulaConteudo {

    @Id
    private UUID id;
    private UUID aulaId;
    private TipoArquivo tipo;
    private String titulo;
    private String url;
    private String key;
    private String mimetype;
    private String vimeo;

    @Version
    private int version;

    // Construtor padrão
    public AulaConteudo() {
    }

    // Construtor com campos principais
    public AulaConteudo(UUID id, UUID aulaId, TipoArquivo tipo, String titulo, String url, String key, String mimetype, String vimeo) {
        this.id = id;
        this.aulaId = aulaId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.url = url;
        this.key = key;
        this.vimeo = vimeo;
        this.mimetype = mimetype;
    }

    public AulaConteudo(TipoArquivo tipo, String titulo) {
        this.tipo = tipo;
        this.titulo = titulo;
    }

    // Método para conversão para DTO (ConteudoResponse)
    public ConteudoResponse toAssembly() {
        return new ConteudoResponse(id, aulaId, tipo, titulo, url, key, mimetype, vimeo);
    }

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getVimeo() {
        return vimeo;
    }

    public void setVimeo(String vimeo) {
        this.vimeo = vimeo;
    }
}
