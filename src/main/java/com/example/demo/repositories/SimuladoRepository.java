package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Simulado;

public interface SimuladoRepository extends ListCrudRepository<Simulado, UUID> {
    
}
