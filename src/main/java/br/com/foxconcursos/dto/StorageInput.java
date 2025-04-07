package br.com.foxconcursos.dto;

import java.io.InputStream;

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

    public String getPrefix() {
        
        if (this.mimeType == null)
            throw new RuntimeException("Não é possível identificar o tipo de arquivo.");

        if (this.mimeType.startsWith("application") || this.mimeType.startsWith("text"))
            return "apostilas/";

        if (this.mimeType.startsWith("image"))
            return "imagens/";

        if (this.mimeType.startsWith("video"))
            return "videos/";
        
        throw new RuntimeException("Tipo de arquivo não identificado.");
    }

    public boolean isFileLargerThan5MB() {
        long fiveMBInBytes = 5 * 1024 * 1024; // 5 MB em bytes
        return fileSize > fiveMBInBytes;
    }    

    // Builder estático
    public static class Builder {
        private InputStream fileInputStream;
        private boolean isPublic = false;
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