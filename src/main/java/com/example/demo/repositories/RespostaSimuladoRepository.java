package com.example.demo.repositories;

import java.util.UUID;

import com.example.demo.domain.RespostaSimulado;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface RespostaSimuladoRepository extends CustomCrudRepository<RespostaSimulado, UUID> {

    RespostaSimulado findBySimuladoIdAndUsuarioId(UUID simuladoId, UUID usuarioId);

}
