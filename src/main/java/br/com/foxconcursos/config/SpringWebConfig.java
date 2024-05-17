package br.com.foxconcursos.config;

import java.util.TimeZone;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@SuppressWarnings("all")
public class SpringWebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        
        DateFormatter formatter = new DateFormatter("dd/MM/yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
        
        DateFormatterRegistrar registrar = new DateFormatterRegistrar();
        registrar.setFormatter(formatter);
        registrar.registerFormatters(registry);
    }
}
