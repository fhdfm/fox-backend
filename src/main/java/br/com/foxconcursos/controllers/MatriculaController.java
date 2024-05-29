package br.com.foxconcursos.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.dto.PagamentoRequest;
import br.com.foxconcursos.dto.TesteMP;
import br.com.foxconcursos.services.MPService;
import br.com.foxconcursos.services.MatriculaService;

@RestController
public class MatriculaController {
    
    private final MatriculaService matriculaService;
    private final MPService mpService;

    public MatriculaController(MatriculaService matriculaService, 
        MPService mpService) {
        this.matriculaService = matriculaService;
        this.mpService = mpService;
    }

    @PostMapping(path = "/api/teste", 
        consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> myeggs(@RequestBody TesteMP testeMP) {
        System.out.println("TesteMP: " + testeMP.toString());
        return ResponseEntity.status(HttpStatus.OK).body(testeMP.toString());
    }

    @PreAuthorize("hasRole('ROLE_ALUNO') || hasRole('ROLE_EXTERNO')")
    @PostMapping(path = "/api/alunos/matricula", 
        consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> matricular(@RequestBody PagamentoRequest request) {
        //UUID matriculaId = this.matriculaService.matricular(request);
        Payment payment = mpService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment != null ? payment.getId() : 0L);
    }

    @PreAuthorize("hasRole('ROLE_ALUNO') || hasRole('ROLE_EXTERNO')")
    @PostMapping(path = "/api/alunos/pre-matricula")
    public ResponseEntity<String> prematricula() throws Exception {
       

            PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                    .id("1234")
                    .title("Games")
                    .description("PS5")
                    .pictureUrl("http://picture.com/PS5")
                    .categoryId("games")
                    .quantity(2)
                    .currencyId("BRL")
                    .unitPrice(new BigDecimal("4000"))
                    .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(items).build();
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

       return ResponseEntity.status(HttpStatus.CREATED).body(preference.getId());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/api/admin/matricula", 
        consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> matricular(@RequestBody MatriculaRequest request) {
        UUID matriculaId = this.matriculaService.matricularContingencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaId);
    }


}
