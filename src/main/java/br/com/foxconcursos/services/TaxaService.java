package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.TaxaCidade;
import br.com.foxconcursos.domain.TaxaEstado;
import br.com.foxconcursos.repositories.TaxaCidadeRepository;
import br.com.foxconcursos.repositories.TaxaEstadoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaxaService {
    private final TaxaEstadoRepository taxaEstadoRepository;
    private final TaxaCidadeRepository taxaCidadeRepository;

    public TaxaService(TaxaEstadoRepository taxaEstadoRepository, TaxaCidadeRepository taxaCidadeRepository) {
        this.taxaEstadoRepository = taxaEstadoRepository;
        this.taxaCidadeRepository = taxaCidadeRepository;
    }

    public List<Map<String, Object>> listarTaxasPorEstado() {
        List<TaxaEstado> estados = taxaEstadoRepository.findAll();
        List<TaxaCidade> cidades = taxaCidadeRepository.findAll();


        Map<String, List<Map<String, Object>>> cidadesPorEstado = cidades.stream()
                .collect(Collectors.groupingBy(
                        TaxaCidade::getEstado,
                        Collectors.mapping(cidade -> Map.of(
                                "id", cidade.getId(),
                                "cidade", cidade.getCidade(),
                                "valor", cidade.getValor()
                        ), Collectors.toList())
                ));


        return estados.stream()
                .sorted(Comparator.comparing(TaxaEstado::getEstado))
                .map(estado -> Map.of(
                        "id", estado.getId(),
                        "estado", estado.getEstado(),
                        "valor", estado.getValor(),
                        "cidades", cidadesPorEstado.getOrDefault(estado.getEstado(), new ArrayList<>())
                ))
                .collect(Collectors.toList());
    }


    public double obterTaxa(String estado, String cidade) {

        return taxaCidadeRepository.findByEstadoAndCidade(estado, cidade)
                .map(TaxaCidade::getValor)
                .map(BigDecimal::doubleValue)
                .orElseGet(() -> {
                    List<TaxaEstado> taxasEstado = taxaEstadoRepository.findByEstado(estado);
                    return taxasEstado.isEmpty() ? 0.00 : taxasEstado.get(0).getValor().doubleValue();
                });
    }


    public TaxaEstado salvarTaxaEstado(TaxaEstado taxaEstado) {
        return taxaEstadoRepository.save(taxaEstado);
    }

    public TaxaCidade salvarTaxaCidade(TaxaCidade taxaCidade) {
        return taxaCidadeRepository.save(taxaCidade);
    }

    public void deletarTaxaCidade(UUID id) {
        taxaCidadeRepository.deleteById(String.valueOf(id));
    }
}
