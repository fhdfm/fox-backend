package br.com.foxconcursos.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.foxconcursos.domain.PerfilUsuario;
import br.com.foxconcursos.domain.StatusUsuario;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface UsuarioRepository extends CustomCrudRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByPerfil(PerfilUsuario nome);
    List<Usuario> findAllByStatus(StatusUsuario status);
    int countByEmail(String email);
    int countByCpf(String cpf);
}
