package br.com.foxconcursos.dto;

import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.domain.TipoArquivo;

import java.util.UUID;

public class AulaConteudoRequest {

    private TipoArquivo tipo;
    private String titulo;
    private UUID videoId;
    private MultipartFile file;

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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public boolean hasMedia() {
        return this.file != null;
    }

}
