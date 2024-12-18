package br.com.foxconcursos.dto;

import java.io.IOException;
import java.io.InputStream;

import br.com.foxconcursos.util.FileTypeChecker;

public class StorageInput {

    private final InputStream fileInputStream; // Conteúdo do arquivo
    private final boolean isPublic;       // Indica se o arquivo é público ou privado
    private final String fileName;        // Nome do arquivo
    private final String mimeType;        // Tipo MIME do arquivo
    private final long fileSize;

    // Construtor privado para ser usado pelo Builder
    private StorageInput(Builder builder) {
        this.fileInputStream = builder.fileInputStream;
        this.isPublic = builder.isPublic;
        this.fileName = builder.fileName;
        this.mimeType = builder.mimeType;
        this.fileSize = builder.fileSize;
    }

    // Getters
    public InputStream getFileInputStream() {
        return fileInputStream;
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

    public long getFileSize() {
        return fileSize;
    }

    public boolean isMovie() throws IOException {
        return FileTypeChecker.isMovie(fileInputStream);
    }

    public boolean isDocument() throws IOException {
        return FileTypeChecker.isDocument(fileInputStream);
    }

    public boolean isImage() throws IOException {
        return FileTypeChecker.isImage(fileInputStream);
    }

    // Builder estático
    public static class Builder {
        private InputStream fileInputStream;
        private boolean isPublic;
        private String fileName;
        private String mimeType;
        private long fileSize;

        // Métodos do Builder
        public Builder withFileInputStream(InputStream fileInputStream) {
            this.fileInputStream = fileInputStream;
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

        public Builder withFileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        // Método para construir o objeto
        public StorageInput build() {
            
            if (fileInputStream == null) {
                throw new IllegalArgumentException("InputStream é obrigatório.");
            }

            if (fileName == null || fileName.isEmpty()) {
                throw new IllegalArgumentException("FileName é obrigatório.");
            }

            if (mimeType == null || mimeType.isEmpty()) {
                throw new IllegalArgumentException("MimeType é obrigatório.");
            }

            if (fileSize == 0) {
                throw new IllegalArgumentException("FileSize é obrigatório.");
            }

            return new StorageInput(this);
        }
    }
}