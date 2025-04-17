package br.com.foxconcursos.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("carrossel")
public class Carrossel {

    @Id
    private UUID id;

    private int ordem;

    private String imagem;

    private String link;

    // Construtores
    public Carrossel() {
    }

    public Carrossel(UUID id, int ordem, String imagem, String link) {
        this.id = id;
        this.ordem = ordem;
        this.imagem = imagem;
        this.link = link;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
