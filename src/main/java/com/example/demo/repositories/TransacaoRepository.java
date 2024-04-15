package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Transacao;

public interface TransacaoRepository extends ListCrudRepository<Transacao, UUID> {
}
