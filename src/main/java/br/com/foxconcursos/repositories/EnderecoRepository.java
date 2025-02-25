package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Endereco;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnderecoRepository extends CustomCrudRepository<Endereco, UUID> {
    Endereco findByUsuarioId(UUID usuarioId);
}
