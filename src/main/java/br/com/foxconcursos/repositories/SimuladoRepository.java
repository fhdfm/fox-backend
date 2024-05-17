package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface SimuladoRepository extends CustomCrudRepository<Simulado, UUID> {
    
    Simulado findByCursoId(UUID cursoId);
    Boolean existsByCursoId(UUID cursoId);

}
