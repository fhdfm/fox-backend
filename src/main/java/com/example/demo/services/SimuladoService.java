package com.example.demo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Disciplina;
import com.example.demo.domain.Simulado;
import com.example.demo.dto.CursoDTO;
import com.example.demo.dto.DisciplinaQuestoesResponse;
import com.example.demo.dto.QuestaoResponse;
import com.example.demo.dto.SimuladoComQuestoesResponse;
import com.example.demo.dto.SimuladoDTO;
import com.example.demo.dto.SimuladoRequest;
import com.example.demo.dto.SimuladoResponse;
import com.example.demo.repositories.SimuladoRepository;

@Service
public class SimuladoService {
    
    private final SimuladoRepository simuladoRepository;
    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;
    private final QuestaoSimuladoService questaoSimuladoService;

    public SimuladoService(SimuladoRepository simuladoRepository, 
        CursoService cursoService, 
        DisciplinaService disciplinaService,
        QuestaoSimuladoService questaoSimuladoService) {
        this.simuladoRepository = simuladoRepository;
        this.cursoService = cursoService;
        this.disciplinaService = disciplinaService;
        this.questaoSimuladoService = questaoSimuladoService;
    }

    public UUID save(SimuladoRequest request) {
        
        validarSimulado(request);
        
        Simulado simulado = new Simulado(request);
        simulado = simuladoRepository.save(simulado);

        return simulado.getId();
    }

    public SimuladoResponse save(UUID id, SimuladoRequest request) {
        
        validarSimulado(request);
        
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        simulado.setTitulo(request.getTitulo());
        simulado.setDescricao(request.getDescricao());
        simulado.setCursoId(request.getCursoId());
        simulado.setAlternativasPorQuestao(request.getAlternativasPorQuestao());
        simulado.setDataInicio(request.getDataInicio());
        simulado.setDuracao(request.getDuracao());
        simulado.setValor(request.getValor());
        simulado = simuladoRepository.save(simulado);
        
        return new SimuladoResponse(simulado);
    }

    private void validarSimulado(SimuladoRequest request) {
        
        if (request.getTitulo() == null || request.getTitulo().isEmpty())
            throw new IllegalArgumentException("Título do simulado é obrigatório.");

        if (request.getDescricao() == null || request.getDescricao().isEmpty())
            throw new IllegalArgumentException("Descrição do simulado é obrigatória.");

        if (request.getCursoId() == null)
            throw new IllegalArgumentException("Curso vinculado ao simulado é obrigatório.");

        if (request.getBancaId() == null)
            throw new IllegalArgumentException("Banca vinculada ao simulado é obrigatória.");

        if (request.getAlternativasPorQuestao() == null 
            || (request.getAlternativasPorQuestao() < 4 && request.getAlternativasPorQuestao() > 5))
            throw new IllegalArgumentException("Número de alternativas por questão deve ser 4 ou 5.");

        if (request.getDataInicio() == null)
            throw new IllegalArgumentException("Data de início do simulado é obrigatória.");

        var hoje = LocalDateTime.now();
        if (request.getDataInicio().isBefore(hoje))
            throw new IllegalArgumentException("Data de início do simulado deve ser maior que: " + hoje);
        
        if (request.getDuracao() == null || request.getDuracao().isEmpty())
            throw new IllegalArgumentException("Duração do simulado é obrigatória.");

        if (request.getValor() == null || request.getValor().compareTo(BigDecimal.ZERO) > 0)
            throw new IllegalArgumentException("Valor do simulado é obrigatório e deve ser maior que 0.");
    }

    public SimuladoComQuestoesResponse findById(UUID id) {
        
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        
        List<Disciplina> disciplinas =
            disciplinaService.findByCursoId(simulado.getCursoId());

        List<DisciplinaQuestoesResponse> disciplinasResponse
            = new ArrayList<DisciplinaQuestoesResponse>();
        
        for (Disciplina disciplina : disciplinas) {
            
            // buscar questões da disciplina
            List<QuestaoResponse> questoes = questaoSimuladoService.findQuestoesBySimuladoIdAndDisciplinaId(
                simulado.getId(), disciplina.getId());

            DisciplinaQuestoesResponse disciplinaResponse = 
                new DisciplinaQuestoesResponse(disciplina, questoes);
            disciplinasResponse.add(disciplinaResponse);
        }

        SimuladoComQuestoesResponse response = 
            new SimuladoComQuestoesResponse(simulado, disciplinasResponse);

        return response;
    }

    public void delete(UUID id) {
        // validar se já existem inscrições ou questoes para o simulado.
        simuladoRepository.deleteById(id);
    }

    public List<SimuladoDTO> findAll() {
        List<Simulado> simulados = simuladoRepository.findAll();
        Map<UUID, CursoDTO> cursos = cursoService.findAllAsMap();
        List<SimuladoDTO> result = new ArrayList<SimuladoDTO>();
        for (Simulado simulado : simulados) {
            SimuladoDTO simuladoDTO = new SimuladoDTO(simulado);
            CursoDTO curso = cursos.get(simuladoDTO.getCursoId());
            simuladoDTO.setNomeCurso(curso.getTitulo());
            simuladoDTO.setBancaId(curso.getBancaId());
            simuladoDTO.setNomeBanca(curso.getNomeBanca());
            result.add(simuladoDTO);
        }
        return result;
    }

}
