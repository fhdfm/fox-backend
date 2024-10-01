package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.SACRequest;
import br.com.foxconcursos.events.TokenEvent;
import br.com.foxconcursos.repositories.PasswordRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RecuperarPasswordService {

    private final PasswordRepository repository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;

    public RecuperarPasswordService(PasswordRepository repository,
                                    EmailService emailService, JwtService jwtService,
                                    ApplicationEventPublisher publisher) {

        this.repository = repository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.publisher = publisher;

    }

    public Password findByToken(String token) {
        return this.repository.findByToken(token);
    }

    @Transactional
    public String recuperarPassword(Usuario user) {

        Password password = this.repository.findByUsuario(user.getId());

        Instant now = Instant.now();

        long expiresAt = 900;

        String token = this.jwtService.generatePasswordToken(user.getEmail(), now, expiresAt);

        if (password == null)
            this.repository.create(token, user.getId());
        else
            this.repository.update(token, user.getId());

        try {
            this.emailService.sendEmail(
                    user.getEmail(),
                    "Recuperação de senha",
                    gerarEmail(user.getNome(), token));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao enviar e-mail.");
        }

        TokenEvent event = new TokenEvent(this);
        event.setUsuarioId(user.getId());
        event.setDataExpiracao(now.plusSeconds(expiresAt));
        event.setTipo(TokenEvent.ADD);

        this.publisher.publishEvent(event);

        return "Link enviado para: " + user.getEmail();
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

    public void deleteByUserId(UUID targetId) {
        this.repository.deleteByUsuarioId(targetId);
    }


    public String enviarEmailSAC(SACRequest sacRequest) throws Exception {

        try {
            this.emailService.sendEmailSAC(
                    sacRequest.getEmail(),
                    sacRequest.getProduto(), gerarConteudoEmailSAC(
                            sacRequest.getNome(),
                            sacRequest.getEmail(),
                            sacRequest.getCelular(),
                            sacRequest.getProduto(),
                            sacRequest.getCidadeCargo(),
                            sacRequest.getEndereco(),
                            sacRequest.getObservacoes()
                    )
            );
            return "Email enviado ";

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao enviar e-mail.");
        }
    }

    public String gerarConteudoEmailSAC(String nome, String email, String celular, String compra, String cidadeCargo, String endereco, String observacoes) {
        return """
                <!DOCTYPE html>
                <html lang="pt-br">
                <head>
                    <meta charset="UTF-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                    <style>
                        body,
                        html {
                            margin: 0;
                            padding: 0;
                            font-family: Arial, sans-serif;
                        }
                        .content {
                            font-size: 16px;
                            color: #333;
                        }
                        .highlight {
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                <div class="content">
                    <h2>Solicitação SAC</h2>
                    <p><span class="highlight">Nome:</span> {NOME}</p>
                    <p><span class="highlight">E-mail:</span> {EMAIL}</p>
                    <p><span class="highlight">Celular:</span> {CELULAR}</p>
                    <p><span class="highlight">Produto Comprado:</span> {COMPRA}</p>
                    <p><span class="highlight">Cidade e Cargo:</span> {CIDADE_CARGO}</p>
                    <p><span class="highlight">Endereço de Entrega:</span> {ENDERECO}</p>
                    <p><span class="highlight">Observações:</span> {OBSERVACOES}</p>
                </div>
                </body>
                </html>
                """
                .replace("{NOME}", nome)
                .replace("{EMAIL}", email)
                .replace("{CELULAR}", celular)
                .replace("{COMPRA}", compra)
                .replace("{CIDADE_CARGO}", cidadeCargo)
                .replace("{ENDERECO}", endereco)
                .replace("{OBSERVACOES}", observacoes);
    }

}
