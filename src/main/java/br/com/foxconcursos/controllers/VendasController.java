package br.com.foxconcursos.controllers;

import br.com.foxconcursos.dto.VendasStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.foxconcursos.dto.VendasFilterResquest;
import br.com.foxconcursos.dto.VendasResponse;
import br.com.foxconcursos.services.PagamentoService;

@RestController
@RequestMapping(value ="/api/vendas", consumes = "application/json", produces = "application/json")
public class VendasController {
    
    private final PagamentoService pagamentoService;

    public VendasController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Page<VendasResponse>> listarVendas(VendasFilterResquest request, Pageable pageable) {
        Page<VendasResponse> vendas = pagamentoService.findByParameters(request, pageable);
        return ResponseEntity.ok(vendas);
    }
    @PutMapping("/status")
    public ResponseEntity<String> atualizarStatus(@RequestBody VendasStatusUpdateRequest request) {
        pagamentoService.atualizarStatusVenda(request);
        return ResponseEntity.ok("Status atualizado com sucesso!");
    }
}
