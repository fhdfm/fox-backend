package br.com.foxconcursos.dto;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.util.FileTypeChecker;

public class StorageInput {

    private final MultipartFile inputStream; // Conteúdo do arquivo
    private final boolean isPublic;       // Indica se o arquivo é público ou privado

    // Construtor privado para ser usado pelo Builder
    private StorageInput(Builder builder) {
        this.inputStream = builder.inputStream;
        this.isPublic = builder.isPublic;
    }

    // Getters
    public MultipartFile getInputStream() {
        return inputStream;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isMovie() throws IOException {
        return FileTypeChecker.isMovie(inputStream);
    }

    // Builder estático
    public static class Builder {
        private MultipartFile inputStream;
        private boolean isPublic;

        // Métodos do Builder
        public Builder withInputStream(MultipartFile inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        // Método para construir o objeto
        public StorageInput build() {
            if (inputStream == null) {
                throw new IllegalArgumentException("InputStream é obrigatório.");
            }
            return new StorageInput(this);
        }
    }
}
