package br.com.foxconcursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication
@EnableScheduling
public class FoxApplication {

	public static void main(String[] args) {

		MercadoPagoConfig.setAccessToken(
			"TEST-5958924722908086-052113-41484799dceaa43dab1ab0f839f2ae83-104350301");

		SpringApplication.run(FoxApplication.class, args);
	}

}
