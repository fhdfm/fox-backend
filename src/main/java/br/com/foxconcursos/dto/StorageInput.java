package br.com.foxconcursos.dto;

import java.io.IOException;
import java.io.InputStream;

import br.com.foxconcursos.util.FileTypeChecker;

public class StorageInput {

    private final InputStream inputStream; // Conteúdo do arquivo
    private final boolean isPublic;       // Indica se o arquivo é público ou privado
    private final String fileName;        // Nome do arquivo
    private final String mimeType;        // Tipo MIME do arquivo

    // Construtor privado para ser usado pelo Builder
    private StorageInput(Builder builder) {
        this.inputStream = builder.inputStream;
        this.isPublic = builder.isPublic;
        this.fileName = builder.fileName;
        this.mimeType = builder.mimeType;
    }

    // Getters
    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isMovie() throws IOException {
        return FileTypeChecker.isMovie(inputStream);
    }

    // Builder estático
    public static class Builder {
        private InputStream inputStream;
        private boolean isPublic;
        private String fileName;
        private String mimeType;

        // Métodos do Builder
        public Builder withInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        // Método para construir o objeto
        public StorageInput build() {
            if (inputStream == null) {
                throw new IllegalArgumentException("InputStream é obrigatório.");
            }
            if (fileName == null || fileName.isEmpty()) {
                throw new IllegalArgumentException("FileName é obrigatório.");
            }
            if (mimeType == null || mimeType.isEmpty()) {
                throw new IllegalArgumentException("MimeType é obrigatório.");
            }
            return new StorageInput(this);
        }
    }
}