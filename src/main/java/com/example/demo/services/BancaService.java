package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Banca;
import com.example.demo.repositories.BancaRepository;
import com.example.demo.util.FoxUtils;

@Service
public class BancaService {
    
    private final BancaRepository bancaRepository;

    public BancaService(BancaRepository bancaRepository) {
        this.bancaRepository = bancaRepository;
    }

    public Banca salvar(Banca banca) {
        
        if (banca == null || banca.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da banca.");
        
        UUID id = banca.getId();    
        if (id == null) {
            if (bancaRepository.existsByNome(banca.getNome()))
                throw new IllegalArgumentException("Banca já cadastrada.");
            return bancaRepository.save(banca);
        } 
            
        Banca bancaDB = bancaRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Banca não encontrada."));
        
        if (!bancaDB.getNome().equals(banca.getNome()) 
            && bancaRepository.existsByNome(banca.getNome())) 
            throw new IllegalArgumentException("Banca já cadastrada.");

        bancaDB.setNome(banca.getNome());
        return bancaRepository.save(bancaDB);
    }

    public List<Banca> findAll() {
        return bancaRepository.findAll();
    }

    public Banca findById(UUID id) {
        return bancaRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Banca não encontrada."));
    }

    public Map<UUID, String> findAllAsMap() {
        return bancaRepository.findAll()
            .stream()
            .collect(
                Collectors.toMap(Banca::getId, Banca::getNome));
    }

    public void delete(UUID id) {
        bancaRepository.deleteById(id);
    }

    public List<Banca> findAll(String query) throws Exception {
        
        if (query == null || query.isBlank())
            return this.findAll();
        
        Banca banca = FoxUtils.criarObjetoDinamico(query, Banca.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos
        
        Example<Banca> example = Example.of(banca, matcher);
        List<Banca> bancas = new ArrayList<Banca>();
        
        Iterable<Banca> bancasIterator = bancaRepository.findAll(example);
        bancasIterator.forEach(bancas::add);
        return bancas;
    }
}
