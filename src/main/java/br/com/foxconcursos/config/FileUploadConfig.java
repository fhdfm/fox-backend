package br.com.foxconcursos.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Definir tamanho máximo do arquivo (5GB)
        factory.setMaxFileSize(DataSize.ofGigabytes(5));
        // Definir tamanho máximo da solicitação (5GB)
        factory.setMaxRequestSize(DataSize.ofGigabytes(5));
        return factory.createMultipartConfig();
    }
}
