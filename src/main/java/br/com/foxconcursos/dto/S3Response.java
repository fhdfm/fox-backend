package br.com.foxconcursos.dto;

public class S3Response {

    private final String key;
    private final String url;
    private final String mimeType;

    // Construtor privado para o builder
    private S3Response(Builder builder) {
        this.key = builder.key;
        this.url = builder.url;
        this.mimeType = builder.mimeType;
    }

    // Getters
    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public StorageOutput get() {
        return new StorageOutput(key, url, mimeType);
    }

    // Builder interno
    public static class Builder {
        private String key;
        private String url;
        private String mimeType;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public S3Response build() {
            return new S3Response(this);
        }
    }

    // Método estático para iniciar o builder
    public static Builder builder() {
        return new Builder();
    }
}
