package br.com.foxconcursos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.dto.CursoDTO;
import br.com.foxconcursos.dto.DataResponse;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.ProdutoSimuladoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.SimuladoRequest;
import br.com.foxconcursos.dto.SimuladoResponse;
import br.com.foxconcursos.dto.SimuladoResumoResponse;
import br.com.foxconcursos.repositories.SimuladoRepository;
import br.com.foxconcursos.util.FoxUtils;

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
        simulado.setQuantidadeQuestoes(0);
        simulado = simuladoRepository.save(simulado);

        return simulado.getId();
    }

    public void incrementarQuestoes(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        simulado.setQuantidadeQuestoes(simulado.getQuantidadeQuestoes() + 1);
        simuladoRepository.save(simulado);
    }

    public ProdutoSimuladoResponse getMatriculaSimulado(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        return new ProdutoSimuladoResponse(simulado);
    }

    public Simulado obterPorId(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        return simulado;
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

        if (request.getValor() == null || request.getValor().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor do simulado é obrigatório e deve ser maior que 0.");
    }

    public SimuladoCompletoResponse findById(UUID id) {
        return this.findById(id, true);
    }

    public SimuladoCompletoResponse findById(UUID id, boolean exibirCorreta) {
        
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
                    simulado.getId(), disciplina.getId(), exibirCorreta);

            DisciplinaQuestoesResponse disciplinaResponse = 
                new DisciplinaQuestoesResponse(disciplina, questoes);
            disciplinasResponse.add(disciplinaResponse);
        }

        SimuladoCompletoResponse response = 
            new SimuladoCompletoResponse(simulado, disciplinasResponse);

        LocalDateTime dataInicio = simulado.getDataInicio();
        response.setDatas(new DataResponse(
            dataInicio, this.calcularHoraFim(
                dataInicio, simulado.getDuracao())));

        return response;
    }

    public LocalDateTime calcularHoraFim(UUID simuladoId) {
        Simulado simulado = simuladoRepository.findById(simuladoId).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + simuladoId));
        return this.calcularHoraFim(
            simulado.getDataInicio(), simulado.getDuracao());
    }

    public LocalDateTime calcularHoraFim(LocalDateTime dataInicio, String duracao) {
        String[] duracaoSplit = duracao.split(":");
        int horas = Integer.parseInt(duracaoSplit[0]);
        int minutos = Integer.parseInt(duracaoSplit[1]);
        dataInicio = dataInicio.plusHours(horas);
        dataInicio = dataInicio.plusMinutes(minutos);
        return dataInicio;
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
                s.getId(), s.getTitulo(), curso.getTitulo(), s.getDataInicio()));
        }

        return result;
    }

    public UUID getCursoAssociado(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        return simulado.getCursoId();
    }

    public UUID findByCursoId(UUID cursoId) {
        Simulado simulado = simuladoRepository.findByCursoId(cursoId);
        return simulado.getId();
    }

    public Boolean existsByCursoId(UUID cursoId) {
        return this.simuladoRepository.existsByCursoId(cursoId);
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
                s.getId(), s.getTitulo(), curso.getTitulo(), s.getDataInicio()));
        }

        return result;
    }

    public int obterQuantidadeQuestoes(UUID id) {
        return simuladoRepository.obterQuantidadeQuestoes(id);
    }

}
