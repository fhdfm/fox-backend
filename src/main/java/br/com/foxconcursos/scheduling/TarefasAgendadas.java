package br.com.foxconcursos.scheduling;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.domain.Password;
import br.com.foxconcursos.repositories.PasswordRepository;
import br.com.foxconcursos.services.JwtService;
import br.com.foxconcursos.services.RespostaSimuladoService;

@Component
public class TarefasAgendadas {
    
    private final PasswordRepository passwordRepository;
    private final JwtService jwtService;
    private final RespostaSimuladoService respostaSimuladoService;

    public TarefasAgendadas(PasswordRepository passwordRepository, 
        JwtService jwtService, 
        RespostaSimuladoService respostaSimuladoService) {
        this.passwordRepository = passwordRepository;
        this.jwtService = jwtService;
        this.respostaSimuladoService = respostaSimuladoService;
    }

    @Scheduled(cron = "0 0 * * * ?") // de hora em hora
    public void limparTokensExpirados() {
       List<Password> passwords = passwordRepository.findAll();
         for (Password password : passwords)
              if (!jwtService.validarToken(password.getToken()))
                passwordRepository.delete(password.getToken());
    }

    //@Scheduled(cron = "0 */5 * * * *") // de 5 em 5 minutos
    public void finalizarSimulado() {

        List<UUID> simulados = respostaSimuladoService
          .recuperarSimuladosNaoFinalizados(LocalDateTime.now());

        if (simulados != null && !simulados.isEmpty()) {
            for (UUID simuladoId : simulados) {
                respostaSimuladoService.finalizarViaJob(simuladoId);
            }
        }
    }

}
