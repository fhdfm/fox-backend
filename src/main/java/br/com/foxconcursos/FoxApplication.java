package br.com.foxconcursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoxApplication.class, args);
	}

}
