package br.com.foxconcursos.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeChecker {
    
    public static boolean isMovie(MultipartFile file) throws IOException {
        
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Arquivo inválido.");

            String detectedType;

            try (InputStream is = file.getInputStream()) {
                Tika tika = new Tika();
                detectedType = tika.detect(is);
            }

            return (detectedType != null && detectedType.startsWith("video/"));
    }

}
