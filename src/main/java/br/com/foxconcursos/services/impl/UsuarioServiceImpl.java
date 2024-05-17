package br.com.foxconcursos.services.impl;

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
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.domain.StatusUsuario;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.UsuarioResponse;
import br.com.foxconcursos.repositories.PasswordRepository;
import br.com.foxconcursos.repositories.UsuarioRepository;
import br.com.foxconcursos.services.EmailService;
import br.com.foxconcursos.services.JwtService;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class UsuarioServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UsuarioRepository usuarioRepository;
    private final PasswordRepository passwordRepository;
    private final JwtService jwtService;
    private final EmailService emailService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, 
        PasswordEncoder encoder, JwtService jwtService, 
        PasswordRepository passwordRepository, 
        EmailService emailService) {
        
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.passwordRepository = passwordRepository;
        this.emailService = emailService;
    }

    @Transactional
    public String recuperarPassword(String email) {
        
      Usuario usuario = this.usuarioRepository.findByEmail(email)
          .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));  
        
      String token = this.jwtService.generatePasswordToken(email);
      
      this.passwordRepository.save(token, usuario.getId());
      
      try {
        this.emailService.sendEmail(email, "Recuperação de senha", 
          "Clique <a href='recuperar-senha?token=" + token + "'>aqui</a> para recuperar sua senha.");      
      } catch (Exception e) { 
        e.printStackTrace();
            throw new IllegalArgumentException("Erro ao enviar e-mail.");
      }

      return "Link enviado para: " + email;
    }

    @Transactional
    public String alterarPassowrd(String token, String password) {
        
        if (!jwtService.validarToken(token)) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        Password passwordEntity = this.passwordRepository.findByToken(token);
        if (passwordEntity == null) {
            throw new IllegalArgumentException("Token inválido.");
        }

        Usuario usuario = this.usuarioRepository.findById(passwordEntity.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setPassword(this.encoder.encode(password));
        this.usuarioRepository.save(usuario);
        this.passwordRepository.delete(token);

        return "Senha alterada com sucesso.";
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
