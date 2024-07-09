package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;

import br.com.foxconcursos.domain.PerfilUsuario;
import br.com.foxconcursos.domain.StatusUsuario;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface UsuarioRepository extends CustomCrudRepository<Usuario, UUID> {

    Optional<Usuario> findByEmailAndStatus(String email, StatusUsuario status);
    Optional<Usuario> findByCpfAndStatus(String cpf, StatusUsuario status);
    List<Usuario> findByPerfil(PerfilUsuario nome);
    Page<Usuario> findAllByStatus(Pageable pageable, StatusUsuario status);
    boolean existsByEmailAndStatus(String email, StatusUsuario status);
    boolean existsByCpfAndStatus(String cpf, StatusUsuario status);
}
