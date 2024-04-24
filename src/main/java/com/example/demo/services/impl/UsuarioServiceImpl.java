package com.example.demo.services.impl;

import java.util.ArrayList;
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

import com.example.demo.domain.StatusUsuario;
import com.example.demo.domain.Usuario;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.UsuarioResponse;
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
        usuario.setStatus(StatusUsuario.ATIVO);
        Usuario savedUser = this.usuarioRepository.save(usuario);
        return this.loadUserByUsername(savedUser.getEmail());
    }

    public UsuarioResponse findById(UUID usuarioId) {
        return this.usuarioRepository.findById(usuarioId)
                    .map(UsuarioResponse::new)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    private List<UsuarioResponse> findAllAtivos() {
        return this.usuarioRepository.findAllByStatus(StatusUsuario.ATIVO)
            .stream()
                .map(UsuarioResponse::new)
                    .collect(Collectors.toList());
    }

    public List<UsuarioResponse> findAll(String filter) throws Exception {

        List<UsuarioResponse> response = new ArrayList<UsuarioResponse>();

        if (filter == null || filter.isBlank())
            return this.findAllAtivos();

        Usuario usuario = FoxUtils.criarObjetoDinamico(filter, Usuario.class);
        usuario.setStatus(StatusUsuario.ATIVO);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos            

        Iterable<Usuario> usuarios = 
            this.usuarioRepository.findAll(
                Example.of(usuario, matcher));
        
        response.addAll(StreamSupport.stream(usuarios.spliterator(), false)
            .map(UsuarioResponse::new)
                .collect(Collectors.toList()));

        return response;

    }

    public void desativar(UUID id) {
        Usuario usuario = this.usuarioRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Usuário não encontrado."));
        usuario.setStatus(StatusUsuario.INATIVO);
        this.usuarioRepository.save(usuario);
    }
}
