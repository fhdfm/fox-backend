package br.com.foxconcursos.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.dto.PerformanceResponse;
import br.com.foxconcursos.services.PerformanceService;

@RestController
@RequestMapping(path = "/api/aluno/performance", produces=MediaType.APPLICATION_JSON_VALUE)
public class PerformanceController {
    
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO')")
    @GetMapping
    public ResponseEntity<PerformanceResponse> getPeformance(
        @RequestParam(name = "mesInicial", required = false) Integer mesInicial,
        @RequestParam(name = "anoInicial", required = false) Integer anoInicial,
        @RequestParam(name = "mesFinal", required = false) Integer mesFinal,
        @RequestParam(name = "anoFinal", required = false) Integer anoFinal) {
        
        PerformanceResponse response = null;
        
        if (mesInicial != null && anoInicial != null && mesFinal != null && anoFinal != null)
            response = this.performanceService.obterPerformanceIntervalo(mesInicial, anoInicial, mesFinal, anoFinal);

        if (mesInicial != null && anoInicial != null && mesFinal == null && anoFinal == null)
            response = this.performanceService.obterPerformanceMesAno(mesInicial, anoInicial);

        if (anoInicial != null && mesInicial == null && anoFinal == null && mesFinal == null)
            response = this.performanceService.obterPerformanceAno(anoInicial);

        if (response == null)
            response = this.performanceService.obterPerformance();
    
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
