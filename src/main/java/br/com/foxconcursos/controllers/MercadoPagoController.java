package br.com.foxconcursos.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.services.MercadoPagoService;

@RestController
public class MercadoPagoController {

    private final MercadoPagoService service;

    public MercadoPagoController(MercadoPagoService service) {
        this.service = service;
    }

    @PostMapping(value = "/mp")
    public ResponseEntity<String> receberNotificacao(
        @RequestHeader(value = "x-signature", required = false) String xSignature,
        @RequestHeader(value = "x-request-id", required = false) String xRequestId,
        @RequestParam(value = "data.id", required = false) String dataId) throws Exception {
        
        if (!StringUtils.hasText(xSignature) 
            || !StringUtils.hasText(xRequestId) || !StringUtils.hasText(dataId))
            new ResponseEntity<>(
                "Parametros obrigatórios não recebidos.", 
                HttpStatus.BAD_REQUEST);
        
        service.processarNotificacao(xSignature, xRequestId, dataId);
        return ResponseEntity.ok("Notificação recebida com sucesso.");
    }

}
