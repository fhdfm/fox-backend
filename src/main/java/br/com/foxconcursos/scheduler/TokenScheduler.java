package br.com.foxconcursos.scheduler;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.repositories.PasswordRepository;
import br.com.foxconcursos.services.JwtService;

@Configuration
public class TokenScheduler {
    
    private final PasswordRepository passwordRepository;
    private final JwtService jwtService;

    public TokenScheduler(PasswordRepository passwordRepository, JwtService jwtService) {
        this.passwordRepository = passwordRepository;
        this.jwtService = jwtService;
    }

    @Scheduled(cron = "0 0 * * * ?") // de hora em hora
    public void limparTokensExpirados() {
       List<Password> passwords = passwordRepository.findAll();
         for (Password password : passwords)
              if (!jwtService.validarToken(password.getToken()))
                passwordRepository.delete(password.getToken());
    }
}
