package br.com.foxconcursos.dto;

import org.springframework.web.multipart.MultipartFile;

public class CarrosselRequest {

    private int ordem;

    private MultipartFile imagem;

    private String link;

    // Construtores
    public CarrosselRequest() {
    }

    public CarrosselRequest(int ordem, MultipartFile imagem, String link) {
        this.ordem = ordem;
        this.imagem = imagem;
        this.link = link;
    }

    // Getters e Setters
    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public MultipartFile getImagem() {
        return imagem;
    }

    public void setImagem(MultipartFile imagem) {
        this.imagem = imagem;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}
