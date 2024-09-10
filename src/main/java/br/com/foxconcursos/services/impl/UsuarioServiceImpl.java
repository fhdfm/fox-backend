package br.com.foxconcursos.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.domain.PerfilUsuario;
import br.com.foxconcursos.domain.StatusUsuario;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.UsuarioResponse;
import br.com.foxconcursos.events.TokenEvent;
import br.com.foxconcursos.repositories.UsuarioRepository;
import br.com.foxconcursos.services.RecuperarPasswordService;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class UsuarioServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UsuarioRepository usuarioRepository;
    private final RecuperarPasswordService recuperarPasswordService;
    private final ApplicationEventPublisher publisher;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, 
        PasswordEncoder encoder, RecuperarPasswordService recuperarPasswordService, 
        ApplicationEventPublisher publisher) {
        
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.recuperarPasswordService = recuperarPasswordService;
        this.publisher = publisher;
    }

    @Override
    public UsuarioLogado loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioLogado user = this.usuarioRepository.findByEmail(email)
                    .map(UsuarioLogado::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        
        if (!user.isEnabled())
            throw new IllegalArgumentException("Usuário inativo.");
        
        return user;
    }

    private Usuario create(Usuario usuario) {
        
        validarUsuario(usuario);

        Usuario newUsuario = new Usuario();
        newUsuario.setNome(usuario.getNome());
        
        if (usuarioRepository.existsByEmailAndStatus(
                usuario.getEmail(), StatusUsuario.ATIVO))
            throw new IllegalArgumentException("Email já cadastrado.");
        
        newUsuario.setEmail(usuario.getEmail());

        if (usuarioRepository.existsByCpfAndStatus(
                usuario.getCpf(), StatusUsuario.ATIVO))
            throw new IllegalArgumentException("CPF já cadastrado.");
        
        newUsuario.setCpf(usuario.getCpf());
        
        newUsuario.setPassword(
            this.encoder.encode(
                usuario.getPassword()));
        
        newUsuario.setStatus(StatusUsuario.ATIVO);
        newUsuario.setRole(PerfilUsuario.ALUNO);

        return this.usuarioRepository.save(newUsuario);
    }

    private Usuario update(Usuario usuario) {
        
        validarUsuario(usuario);

        Usuario savedUsuario = this.usuarioRepository.findById(usuario.getId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        savedUsuario.setNome(usuario.getNome());
        
        Optional<Usuario> user = this.usuarioRepository.findByCpfAndStatus(
            usuario.getEmail(), StatusUsuario.ATIVO);
        
        if (user.isPresent() && !user.get().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("E-mail já cadastrado.");

        savedUsuario.setEmail(usuario.getEmail());

        user = this.usuarioRepository.findByCpfAndStatus(
            usuario.getCpf(), StatusUsuario.ATIVO);
        
        if (user.isPresent() && !user.get().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("CPF já cadastrado.");
        
        savedUsuario.setCpf(usuario.getCpf());

        savedUsuario.setTelefone(usuario.getTelefone());
        savedUsuario.setStatus(usuario.getStatus());
        
        return this.usuarioRepository.save(savedUsuario);
    }

    private void validarUsuario(Usuario usuario) {

        if (usuario == null) 
            throw new IllegalArgumentException("Usuario não pode ser nulo.");
        
        if (usuario.getNome() == null || usuario.getNome().isBlank())
            throw new IllegalArgumentException("Nome é obrigatório.");
        
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) 
            throw new IllegalArgumentException("Email é obrigatório.");

        if (usuario.getCpf() == null || usuario.getCpf().isBlank())
            throw new IllegalArgumentException("CPF é obrigatório.");
        else 
            usuario.setCpf(FoxUtils.validarCpf(usuario.getCpf()));
    }

    public Usuario save(Usuario usuario) {
        
        Usuario savedUsuario = null;        
        if (usuario.getId() == null)
            savedUsuario = this.create(usuario);
        else
            savedUsuario = this.update(usuario);

        return savedUsuario;
    }

    @Transactional
    public String alterarPassowrd(String token, String password) {

        Password passwordEntity = this.recuperarPasswordService.findByToken(token);
        if (passwordEntity == null) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        Usuario usuario =
            this.usuarioRepository.findById(passwordEntity.getUsuarioId()).orElseThrow(
                () -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setPassword(this.encoder.encode(password));
        this.usuarioRepository.save(usuario);
        
        this.recuperarPasswordService.deleteByUserId(usuario.getId());

        TokenEvent event = new TokenEvent(this);
        event.setUsuarioId(usuario.getId());
        event.setTipo(TokenEvent.REMOVE);
        
        this.publisher.publishEvent(event);

        return "Senha alterada com sucesso.";
    }    

    public UsuarioResponse findById(UUID usuarioId) {
        return this.usuarioRepository.findById(usuarioId)
                    .map(UsuarioResponse::new)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    private Page<UsuarioResponse> findAllAtivos(Pageable pageable) {
        return this.usuarioRepository.findAllByStatus(pageable, StatusUsuario.ATIVO)
                .map(UsuarioResponse::new);
    }

    public Page<UsuarioResponse> findAll(Pageable pageable, String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAllAtivos(pageable);

        Usuario usuario = FoxUtils.criarObjetoDinamico(filter, Usuario.class);
        //usuario.setStatus(StatusUsuario.ATIVO)
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact()) // Correspondência igual
            .withMatcher("perfil", ExampleMatcher.GenericPropertyMatchers.exact()) // Correspondência parcial
            .withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("telefone", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos            

        Example<Usuario> example = Example.of(usuario, matcher);

        Page<Usuario> usuarios = 
            this.usuarioRepository.findAll(example, pageable);
        
        Page<UsuarioResponse> response = usuarios.map(UsuarioResponse::new);

        return response;

    }

    public void desativar(UUID id) {
        Usuario usuario = this.usuarioRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Usuário não encontrado."));
        usuario.setStatus(StatusUsuario.INATIVO);
        this.usuarioRepository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        return this.usuarioRepository.findByEmailAndStatus(email, StatusUsuario.ATIVO)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado. Verifique se digitou corretamente."));
    }
}
