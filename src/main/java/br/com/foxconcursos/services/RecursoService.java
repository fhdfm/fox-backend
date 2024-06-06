package br.com.foxconcursos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Recurso;
import br.com.foxconcursos.domain.StatusRecurso;
import br.com.foxconcursos.dto.AbrirRecursoRequest;
import br.com.foxconcursos.repositories.RecursoRepository;

@Service
public class RecursoService {
    
    private final RecursoRepository recursoRepository;
    private final AuthenticationService authenticationService;

    public RecursoService(RecursoRepository recursoRepository, 
        AuthenticationService authenticationService) {
        
        this.recursoRepository = recursoRepository;
        this.authenticationService = authenticationService;
    }

    public List<Recurso> findByUsuarioId(UUID usuarioId) {
        return recursoRepository.findByUsuarioId(usuarioId);
    }

    public List<Recurso> findBySimuladoId(UUID simuladoId) {
        return recursoRepository.findBySimuladoId(simuladoId);
    }

    public List<Recurso> findAll() {
        return recursoRepository.findAll();
    }

    public UUID abrirRecurso(AbrirRecursoRequest request) {

        if (request.getQuestaoId() == null) {
            throw new IllegalArgumentException("Questão não informada");
        }

        if (request.getFundamentacao() == null || request.getFundamentacao().isEmpty()) {
            throw new IllegalArgumentException("Fundamentação não informada");
        }

        UUID usuarioId = authenticationService.obterUsuarioLogado();

        Recurso recurso = new Recurso();
        recurso.setQuestaoId(request.getQuestaoId());
        recurso.setFundamentacao(request.getFundamentacao());
        recurso.setStatus(StatusRecurso.EM_ANALISE);
        recurso.setUsuarioId(usuarioId);
        recurso.setDataAbertura(LocalDateTime.now());

        this.recursoRepository.save(recurso);

        return recurso.getId();
    }

    public void deferirRecurso(UUID recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
            .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado"));

        recurso.setStatus(StatusRecurso.DEFERIDO);
        
        // TODO: Enviar e-mail para o usuário informando que o recurso foi deferido
        // complementar com o conteúdo do recurso

        recursoRepository.save(recurso);
    }

    public void indeferirRecurso(UUID recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
            .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado"));

        recurso.setStatus(StatusRecurso.INDEFERIDO);

        // TODO: Enviar e-mail para o usuário informando que o recurso foi deferido
        // complementar com o conteúdo do recurso
    }

}
