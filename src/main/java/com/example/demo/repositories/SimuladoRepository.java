package com.example.demo.repositories;

import java.util.UUID;

import com.example.demo.domain.Simulado;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface SimuladoRepository extends CustomCrudRepository<Simulado, UUID> {
    
}
