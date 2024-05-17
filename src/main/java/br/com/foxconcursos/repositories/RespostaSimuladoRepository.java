package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.RespostaSimulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface RespostaSimuladoRepository extends CustomCrudRepository<RespostaSimulado, UUID> {

    RespostaSimulado findBySimuladoIdAndUsuarioId(UUID simuladoId, UUID usuarioId);

}
