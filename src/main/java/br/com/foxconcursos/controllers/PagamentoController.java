package br.com.foxconcursos.controllers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.foxconcursos.dto.ProdutoMercadoPagoRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferenceBackUrls;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPException;

import br.com.foxconcursos.services.PagamentoService;

@RestController
@RequestMapping(path = "/api/aluno/pagamento", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class PagamentoController {
    
    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
//        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN_TESTE);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
    @PostMapping
    public ResponseEntity<String> create(@RequestParam(value = "productId", required = false) UUID productId) {
        return ResponseEntity.ok(service.registrarPreCompra(productId));
    }

//    private static final String ACCESS_TOKEN = "APP_USR-4247778987129008-021619-e002a2a551b985ef3ae2329f7a4e3d00-436233504";
    private static final String ACCESS_TOKEN_TESTE = "APP_USR-7357929501968350-021813-53a43513983b5adf10dceddb0196baf5-2274204587";

    @PostMapping("/pagar")
    public ResponseEntity<String> createPayment(
            @RequestBody ProdutoMercadoPagoRequest produto
    ) {
        return ResponseEntity.ok(service.pagar(produto));
    }

}
