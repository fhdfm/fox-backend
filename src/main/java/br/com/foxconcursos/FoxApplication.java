package br.com.foxconcursos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication
public class FoxApplication {

    @Value("${integracao.mercadopago.access-token}")
    private String accessToken;

    public static void main(String[] args) {
        // Inicializa o contexto do Spring
        ConfigurableApplicationContext context = SpringApplication.run(FoxApplication.class, args);
        
        // Recupera a instância do FoxApplication para acessar a propriedade injetada
        FoxApplication app = context.getBean(FoxApplication.class);
        
        // Agora você pode acessar o accessToken e passá-lo para a configuração
        MercadoPagoConfig.setAccessToken(app.accessToken);
    }
}
