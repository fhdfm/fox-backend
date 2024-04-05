package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Banca;

public interface BancaRepository extends ListCrudRepository<Banca, UUID> {
    
}