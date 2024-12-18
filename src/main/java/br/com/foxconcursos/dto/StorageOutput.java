package br.com.foxconcursos.dto;

public class StorageOutput {
    
    private String key;
    private String url;
    private String mimeType;

    public StorageOutput(String key, String url, String mimeType) {
        this.key = key;
        this.mimeType = mimeType;
        this.url = url;
    }

    public String getKey() {
        return this.key;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMimeType() {
        return this.mimeType;
    }

}
