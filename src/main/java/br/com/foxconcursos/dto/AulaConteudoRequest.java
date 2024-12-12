package br.com.foxconcursos.dto;

import java.io.InputStream;

import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.domain.TipoArquivo;

public class AulaConteudoRequest {

    private TipoArquivo tipo;
    private String titulo;
    private InputStream file;
    private String fileName; // Nome do arquivo

    // Construtor padrão
    public AulaConteudoRequest() {}

    public AulaConteudo toModel() {
        return new AulaConteudo(tipo, titulo);
    }

    public void validate(boolean isUpdate) {

        if (tipo == null) {
            throw new IllegalArgumentException("O campo 'tipo' é obrigatório.");
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'titulo' é obrigatório.");
        }

        // Validação condicional para `video` e `apostila`
        if (!isUpdate) { // Apenas em criação
            if (tipo == TipoArquivo.VIDEO && file == null) {
                throw new IllegalArgumentException("O campo 'video' é obrigatório para o tipo 'VIDEO'.");
            }
            if (tipo == TipoArquivo.APOSTILA && file == null) {
                throw new IllegalArgumentException("O campo 'apostila' é obrigatório para o tipo 'APOSTILA'.");
            }
        }
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

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean hasMedia() {
        return this.file != null;
    }

}