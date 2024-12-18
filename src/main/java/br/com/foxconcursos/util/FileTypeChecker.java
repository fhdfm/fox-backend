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

    public static boolean isDocument(InputStream is) throws IOException {
        Tika tika = new Tika();
        String detectedType = tika.detect(is);
    
        // Tipos comuns de documentos
        return detectedType != null && (
                detectedType.equals("application/pdf") || // PDF
                detectedType.equals("application/msword") || // Word (doc)
                detectedType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml") || // Word (docx)
                detectedType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml") || // Excel (xlsx)
                detectedType.startsWith("application/vnd.ms-excel") || // Excel (xls)
                detectedType.startsWith("application/vnd.openxmlformats-officedocument.presentationml") || // PowerPoint (pptx)
                detectedType.startsWith("application/vnd.ms-powerpoint") // PowerPoint (ppt)
        );
    }
    
    public static boolean isImage(InputStream is) throws IOException {
        Tika tika = new Tika();
        String detectedType = tika.detect(is);
    
        // Tipos comuns de imagens
        return detectedType != null && detectedType.startsWith("image/");
    }    

}
