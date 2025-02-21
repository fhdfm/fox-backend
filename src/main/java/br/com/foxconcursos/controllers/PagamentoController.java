package br.com.foxconcursos.controllers;

import java.util.UUID;

import br.com.foxconcursos.dto.ProdutoMercadoPagoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mercadopago.MercadoPagoConfig;

import br.com.foxconcursos.services.PagamentoService;

@RestController
@RequestMapping(path = "/api/aluno/pagamento", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class PagamentoController {

    @Value("${integracao.mercadopago.access-token}")
    private String ACCESS_TOKEN;
    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    public ResponseEntity<String> createPayment(
            @RequestBody ProdutoMercadoPagoRequest produto
    ) {
        return ResponseEntity.ok(service.pagar(produto));
    }

}
