package br.com.foxconcursos.dto;

import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.domain.TipoArquivo;
import org.springframework.web.multipart.MultipartFile;

public class VimeoRequest {

    private String titulo;
    private String iframe;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIframe() {
        return iframe;
    }

    public void setIframe(String iframe) {
        this.iframe = iframe;
    }
}
