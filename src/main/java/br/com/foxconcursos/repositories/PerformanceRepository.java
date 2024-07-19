package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.foxconcursos.domain.Performance;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

@Repository
public interface PerformanceRepository extends CustomCrudRepository<Performance, UUID> {
    
    Performance findByUsuarioIdAndMesAndAno(UUID usuarioId, int mes, int ano);

}
