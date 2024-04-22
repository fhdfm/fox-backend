package com.example.demo.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Usuario;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.util.FoxUtils;

@Service
public class UsuarioServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, 
        PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public UsuarioLogado loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.usuarioRepository.findByEmail(email)
                    .map(UsuarioLogado::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public UsuarioLogado save(Usuario usuario) {
        
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario não pode ser nulo.");
        }
        usuario.setPassword(this.encoder.encode(usuario.getPassword()));
        Usuario savedUser = this.usuarioRepository.save(usuario);
        return this.loadUserByUsername(savedUser.getEmail());
    }

    public UsuarioLogado findById(UUID usuarioId) {
        return this.usuarioRepository.findById(usuarioId)
                    .map(UsuarioLogado::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public List<Usuario> findAll(String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAll();

        Usuario usuario = FoxUtils.criarObjetoDinamico(filter, Usuario.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos            

        Iterable<Usuario> usuarios = 
            this.usuarioRepository.findAll(
                Example.of(usuario, matcher));
        
        return StreamSupport.stream(usuarios.spliterator(), false)
            .collect(Collectors.toList());

    }

    public List<Usuario> findAll() {
        return this.usuarioRepository.findAll();
    }

}
