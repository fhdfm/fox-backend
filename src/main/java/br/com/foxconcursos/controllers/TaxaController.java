package br.com.foxconcursos.controllers;
import br.com.foxconcursos.domain.TaxaCidade;
import br.com.foxconcursos.domain.TaxaEstado;
import br.com.foxconcursos.services.TaxaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/taxas")
public class TaxaController {

    private final TaxaService taxaService;

    public TaxaController(TaxaService taxaService) {
        this.taxaService = taxaService;
    }

    @GetMapping("/{estado}/{cidade}")
    public ResponseEntity<Double> obterTaxa(@PathVariable String estado, @PathVariable String cidade) {
        double taxa = taxaService.obterTaxa(estado, cidade);
        return ResponseEntity.ok(taxa);
    }

    @GetMapping("/estados")
    public ResponseEntity<List<Map<String, Object>>> listarTaxasEstado() {
        return ResponseEntity.ok(taxaService.listarTaxasPorEstado());
    }

    @PostMapping("/estados")
    public ResponseEntity<TaxaEstado> salvarTaxaEstado(@RequestBody TaxaEstado taxaEstado) {
        return ResponseEntity.ok(taxaService.salvarTaxaEstado(taxaEstado));
    }

    @PostMapping("/cidades")
    public ResponseEntity<TaxaCidade> salvarTaxaCidade(@RequestBody TaxaCidade taxaCidade) {
        return ResponseEntity.ok(taxaService.salvarTaxaCidade(taxaCidade));
    }

    @DeleteMapping("/cidades/{id}")
    public ResponseEntity<Void> deletarTaxaCidade(@PathVariable UUID id) {
        taxaService.deletarTaxaCidade(id);
        return ResponseEntity.noContent().build();
    }
}
