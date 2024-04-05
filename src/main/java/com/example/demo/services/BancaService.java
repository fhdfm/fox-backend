package com.example.demo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Banca;
import com.example.demo.repositories.BancaRepository;

@Service
public class BancaService {
    
    private final BancaRepository bancaRepository;

    public BancaService(BancaRepository bancaRepository) {
        this.bancaRepository = bancaRepository;
    }

    public Banca salvar(Banca banca) {
        if (banca == null || banca.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da banca.");
        return bancaRepository.save(banca);
    }

    public List<Banca> findAll() {
        return bancaRepository.findAll();
    }

    public Banca findById(UUID id) {
        return bancaRepository.findById(id).orElse(null);
    }
}
