package com.example.demo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Disciplina;
import com.example.demo.domain.Simulado;
import com.example.demo.dto.CursoDTO;
import com.example.demo.dto.DisciplinaQuestoesResponse;
import com.example.demo.dto.QuestaoSimuladoResponse;
import com.example.demo.dto.SimuladoCompletoResponse;
import com.example.demo.dto.SimuladoRequest;
import com.example.demo.dto.SimuladoResponse;
import com.example.demo.dto.SimuladoResumoResponse;
import com.example.demo.repositories.SimuladoRepository;
import com.example.demo.util.FoxUtils;

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

    public SimuladoCompletoResponse findById(UUID id) {
        
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        
        List<Disciplina> disciplinas =
            disciplinaService.findByCursoId(simulado.getCursoId());

        List<DisciplinaQuestoesResponse> disciplinasResponse
            = new ArrayList<DisciplinaQuestoesResponse>();
        
        for (Disciplina disciplina : disciplinas) {
            
            // buscar questões da disciplina
            List<QuestaoSimuladoResponse> questoes =
                questaoSimuladoService.findQuestoesBySimuladoIdAndDisciplinaId(
                    simulado.getId(), disciplina.getId());

            DisciplinaQuestoesResponse disciplinaResponse = 
                new DisciplinaQuestoesResponse(disciplina, questoes);
            disciplinasResponse.add(disciplinaResponse);
        }

        SimuladoCompletoResponse response = 
            new SimuladoCompletoResponse(simulado, disciplinasResponse);

        return response;
    }

    public void delete(UUID id) {
        // validar se já existem inscrições ou questoes para o simulado.
        simuladoRepository.deleteById(id);
    }

    public List<SimuladoResumoResponse> findAll() {
        
        List<Simulado> simulados = simuladoRepository.findAll();
        
        List<SimuladoResumoResponse> result =
            new ArrayList<SimuladoResumoResponse>();

        for (Simulado s : simulados) {
            CursoDTO curso = cursoService.findById(s.getCursoId());
            result.add(new SimuladoResumoResponse(
                s.getId(), s.getTitulo(), curso.getDescricao(), s.getDataInicio()));
        }

        return result;
    }

    public UUID getCursoAssociado(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        return simulado.getCursoId();
    }

    public List<SimuladoResumoResponse> findByExample(String filter) throws Exception {
       
        Simulado simulado = FoxUtils.criarObjetoDinamico(filter, Simulado.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
            .withIgnoreCase() // Ignorar case
            .withIgnoreNullValues(); // Ignorar valores nulos
        
        Example<Simulado> example = Example.of(simulado, matcher);
              
        Iterable<Simulado> simulados = simuladoRepository.findAll(example);
        
        List<SimuladoResumoResponse> result = new ArrayList<SimuladoResumoResponse>();

        for (Simulado s : simulados) {
            CursoDTO curso = cursoService.findById(s.getCursoId());
            result.add(new SimuladoResumoResponse(
                s.getId(), s.getTitulo(), curso.getDescricao(), s.getDataInicio()));
        }

        return result;
    }

}
