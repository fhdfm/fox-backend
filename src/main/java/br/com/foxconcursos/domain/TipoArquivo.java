package br.com.foxconcursos.domain;

public enum TipoArquivo {

    APOSTILA("Apostila"), VIDEO("Vídeo");
    
    private String tipoArquivo;

    TipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getTipoArquivo() {
        return this.tipoArquivo;
    }
    
}
