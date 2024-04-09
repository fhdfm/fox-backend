package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.QuestaoSimulado;

public interface QuestaoSimuladoRepository extends ListCrudRepository<QuestaoSimulado, UUID> {

    List<QuestaoSimulado> findBySimuladoIdOrderByOrdem(UUID simuladoId);
}
