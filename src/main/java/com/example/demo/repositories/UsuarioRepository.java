package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.example.demo.domain.Usuario;

public interface UsuarioRepository extends ListCrudRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

}
