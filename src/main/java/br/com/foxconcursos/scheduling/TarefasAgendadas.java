package br.com.foxconcursos.scheduling;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.repositories.PasswordRepository;
import br.com.foxconcursos.services.JwtService;

@Component
public class TarefasAgendadas {
    
    private final PasswordRepository passwordRepository;
    private final JwtService jwtService;

    public TarefasAgendadas(PasswordRepository passwordRepository, 
        JwtService jwtService) {
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
