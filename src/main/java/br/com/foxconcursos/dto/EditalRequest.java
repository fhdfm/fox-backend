package br.com.foxconcursos.dto;

import org.springframework.web.multipart.MultipartFile;

public class EditalRequest {

    private String titulo;
    private String cidade;
    private String uf;
    private Integer ano;
    private MultipartFile arquivo;

    // Getters e setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public MultipartFile getArquivo() {
        return arquivo;
    }

    public void setArquivo(MultipartFile arquivo) {
        this.arquivo = arquivo;
    }
}

