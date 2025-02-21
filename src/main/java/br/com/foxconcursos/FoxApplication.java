package br.com.foxconcursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication
public class FoxApplication {

	public static void main(String[] args) {

		MercadoPagoConfig.setAccessToken("APP_USR-7357929501968350-021813-53a43513983b5adf10dceddb0196baf5-2274204587");

		SpringApplication.run(FoxApplication.class, args);
	}

}
