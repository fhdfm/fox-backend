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
        this.emailService.sendEmail(
            email, 
            "Recuperação de senha", 
            gerarEmail(usuario.getNome(), token));      
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
            throw new IllegalArgumentException("Token inválido ou expirado.");
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
        UsuarioLogado user = this.usuarioRepository.findByEmail(email)
                    .map(UsuarioLogado::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        
        if (!user.isEnabled())
            throw new IllegalArgumentException("Usuário inativo.");
        
        return user;
    }

    public UsuarioLogado save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario não pode ser nulo.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }

        if (this.usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
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

    private String gerarEmail(String nome, String token) {
        return 
            """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <style>
                body,
                html {
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    font-family: Arial, sans-serif;
                }
        
                .wrapper {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                    background-color: #06050e;
                }
        
                .card {
                    width: 40%;
                    text-align: center;
                    margin: 100px 0;
                    padding: 20px;
                    background-color: white;
                    border-radius: 10px;
                }
        
                .titulo {
                    font-size: 24px;
                    font-weight: 700;
                }
        
                .content {
                    font-size: 18px;
                }
        
                .card_senha {
                    background-color: rgb(255, 68, 0);
                    padding: 1rem 3rem;
                    border-radius: 12px;
                    border: none;
                    margin: 30px 0 0 0 ;
                    cursor: pointer;
                    color: white;
                    font-weight: 600;
                    font-size: 1.3rem;
                }
        
                .card_senha:hover {
                    background-color: rgb(206, 58, 5);
                    transition: 0.5s;
                }
            </style>
        </head>
        <body>
        <div class="wrapper">
            <table
                    role="presentation"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                    width="100%"
            >
                <tr>
                    <td align="center">
                        <div class="card">
                            <!-- Conteúdo do card -->
                            <span class="titulo">
                                Olá {NOME_ALUNO}!
                            </span>
                            <br/>
                            <p class="subtitulo">
                                Clique no link abaixo para que você possa alterar sua nova senha. Você será redirecionado para
                                uma página do <strong style="color: rgb(255, 68, 0);">Fox Concursos</strong>!
                            </p>
        
                            <a class="card_senha" href="https://www.foxconcursos.com.br/security/recuperar-senha?token={TOKEN}">Clique aqui!</a>
        
                        </div>
                    </td>
                </tr>
        
            </table>
        </div>
        </body>
        </html>
        """.replace("{NOME_ALUNO}", nome).replace("{TOKEN}", token);
    }
}
