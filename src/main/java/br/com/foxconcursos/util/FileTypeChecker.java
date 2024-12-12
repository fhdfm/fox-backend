package br.com.foxconcursos.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;

public class FileTypeChecker {
    
    public static boolean isMovie(InputStream is) throws IOException {
        
        Tika tika = new Tika();
        String detectedType = tika.detect(is);
            

        return (detectedType != null && detectedType.startsWith("video/"));
    }

}
