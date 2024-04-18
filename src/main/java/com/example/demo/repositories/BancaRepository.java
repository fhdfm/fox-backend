package com.example.demo.repositories;

import java.util.UUID;

import com.example.demo.domain.Banca;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface BancaRepository extends CustomCrudRepository<Banca, UUID> {

   Boolean existsByNome(String nome);

}