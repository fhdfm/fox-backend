package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.PerfilUsuario;
import com.example.demo.domain.StatusUsuario;
import com.example.demo.domain.Usuario;
import com.example.demo.repositories.custom.CustomCrudRepository;

public interface UsuarioRepository extends CustomCrudRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByPerfil(PerfilUsuario nome);
    List<Usuario> findAllByStatus(StatusUsuario status);
}
