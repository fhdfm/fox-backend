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

    public static TipoArquivo fromString(String tipoArquivo) {
        for (TipoArquivo tipo : TipoArquivo.values()) {
            if (tipo.getTipoArquivo().equalsIgnoreCase(tipoArquivo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum enum correspondente para: " + tipoArquivo);
    }    
    
}
