package br.com.foxconcursos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.dto.DataResponse;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.ProdutoSimuladoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.SimuladoRequest;
import br.com.foxconcursos.dto.SimuladoResponse;
import br.com.foxconcursos.dto.SimuladoResumoResponse;
import br.com.foxconcursos.events.SimuladoEvent;
import br.com.foxconcursos.repositories.SimuladoRepository;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class SimuladoService {

    private Logger logger = LoggerFactory.getLogger(SimuladoService.class);

    private final SimuladoRepository simuladoRepository;
    private final DisciplinaService disciplinaService;
    private final QuestaoSimuladoService questaoSimuladoService;
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SimuladoService(SimuladoRepository simuladoRepository, 
        DisciplinaService disciplinaService,
        QuestaoSimuladoService questaoSimuladoService,
        JdbcTemplate jdbcTemplate, 
        ApplicationEventPublisher applicationEventPublisher) {
        
        this.simuladoRepository = simuladoRepository;
        this.disciplinaService = disciplinaService;
        this.questaoSimuladoService = questaoSimuladoService;
        this.jdbcTemplate = jdbcTemplate;
        this.applicationEventPublisher = applicationEventPublisher;

    }

    @Transactional
    public UUID save(SimuladoRequest request) {
        
        validarSimulado(request);
        
        Simulado simulado = new Simulado(request);
        simulado.setQuantidadeQuestoes(0);
        simulado = simuladoRepository.save(simulado);

        applicationEventPublisher.publishEvent(
            new SimuladoEvent(this, simulado));

        return simulado.getId();
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

    @Transactional
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

        applicationEventPublisher.publishEvent(
            new SimuladoEvent(this, simulado));
        
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

    @Cacheable(value = "simuladoCache", key = "#id", condition = "#useCache")
    private Simulado carregarSimulado(UUID id, boolean useCache) {
        return simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
    }

    @Cacheable(value = "simuladoCache", key = "'disciplinas_curso_' + #cursoId", condition = "#useCache")
    private List<Disciplina> carregarDisciplinas(UUID cursoId, boolean useCache) {
        return disciplinaService.findByCursoId(cursoId);
    }

    @Cacheable(value = "simuladoCache", 
        key = "'questoes_' + #simuladoId + '_' + #disciplinaId + '_' + #exibirCorreta", 
        condition = "#useCache")
    private List<QuestaoSimuladoResponse> carregarQuestoes(UUID simuladoId, 
        UUID disciplinaId, boolean exibirCorreta, boolean useCache) {
        
        return questaoSimuladoService.findQuestoesBySimuladoIdAndDisciplinaId(
            simuladoId, disciplinaId, exibirCorreta);
    }

    public void prepararSimulado(UUID simuladoId) {
        Simulado simulado = this.carregarSimulado(simuladoId, true);
        
        List<Disciplina> disciplinas = this.carregarDisciplinas(
            simulado.getCursoId(), true);

        for (Disciplina disciplina : disciplinas) {
            this.carregarQuestoes(simulado.getId(), disciplina.getId(), 
                true, true);
            this.carregarQuestoes(simulado.getId(), disciplina.getId(), 
                false, true);
        }
    }

    private SimuladoCompletoResponse carregarSimuladoCompleto(UUID id, boolean exibirCorreta, 
        boolean useCache) {
        
        Simulado simulado = this.carregarSimulado(id, useCache);

        CompletableFuture<List<Disciplina>> disciplinasFuture = CompletableFuture.supplyAsync(
             () -> this.carregarDisciplinas(simulado.getCursoId(), useCache)
        );
        
        List<DisciplinaQuestoesResponse> disciplinasResponse;
        try {
            List<Disciplina> disciplinas = disciplinasFuture.get();
            
            List<CompletableFuture<DisciplinaQuestoesResponse>> futureResponses = disciplinas.stream()
                .map(disciplina -> CompletableFuture.supplyAsync(() -> {
                    List<QuestaoSimuladoResponse> questoes = carregarQuestoes(simulado.getId(), 
                        disciplina.getId(), exibirCorreta, useCache);

                    questoes.sort(Comparator.comparingInt(QuestaoSimuladoResponse::getOrdem));
                    return new DisciplinaQuestoesResponse(disciplina, questoes);
                }))
                .collect(Collectors.toList());
            
            disciplinasResponse = futureResponses.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao carregar disciplinas e questões do simulado: " + id, e);
            throw new RuntimeException("Erro ao carregar disciplinas e questões do simulado: " + id);
        }

        disciplinasResponse.sort(Comparator.comparingInt(disciplinaResponse ->
        disciplinaResponse.getQuestoes().isEmpty() ? 
            Integer.MAX_VALUE : disciplinaResponse.getQuestoes().get(0).getOrdem()));

        SimuladoCompletoResponse response = new SimuladoCompletoResponse(simulado, disciplinasResponse);

        LocalDateTime dataInicio = simulado.getDataInicio();
        response.setDatas(new DataResponse(
            dataInicio, FoxUtils.calcularHoraFimSimulado(dataInicio, 
                simulado.getDuracao())
        ));

        return response;            
    }

    public SimuladoCompletoResponse findById(UUID id, boolean exibirCorreta) {
        return this.carregarSimuladoCompleto(id, exibirCorreta, true);
    }

    public SimuladoCompletoResponse findById(UUID id) {
        return this.carregarSimuladoCompleto(id, true, true);
    }

    public SimuladoCompletoResponse findByIdSemCache(UUID id, boolean exibirCorreta) {
        return this.carregarSimuladoCompleto(id, exibirCorreta, false);
    }

    public SimuladoCompletoResponse findByIdSemCache(UUID id) {
        return this.carregarSimuladoCompleto(id, true, false);
    }
    
    // public SimuladoCompletoResponse findById2(UUID id, boolean exibirCorreta) {
        
    //     Simulado simulado = simuladoRepository.findById(id).orElseThrow(
    //         () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        
    //     List<Disciplina> disciplinas =
    //         disciplinaService.findByCursoId(simulado.getCursoId());

    //     List<DisciplinaQuestoesResponse> disciplinasResponse
    //         = new ArrayList<DisciplinaQuestoesResponse>();
        
    //     for (Disciplina disciplina : disciplinas) {
            
    //         // buscar questões da disciplina
    //         List<QuestaoSimuladoResponse> questoes =
    //             questaoSimuladoService.findQuestoesBySimuladoIdAndDisciplinaId(
    //                 simulado.getId(), disciplina.getId(), exibirCorreta);

    //         Collections.sort(questoes, Comparator.comparingInt(
    //             QuestaoSimuladoResponse::getOrdem));            

    //         DisciplinaQuestoesResponse disciplinaResponse = 
    //             new DisciplinaQuestoesResponse(disciplina, questoes);
    //         disciplinasResponse.add(disciplinaResponse);
    //     }

    //     // ordenar disciplinasResponse pela ordem da primeira questão de cada disciplina
    //     Collections.sort(disciplinasResponse, Comparator.comparingInt(disciplinaResponse -> 
    //         disciplinaResponse.getQuestoes().isEmpty() ? 
    //             Integer.MAX_VALUE : disciplinaResponse.getQuestoes().get(0).getOrdem()
    //     ));        

    //     SimuladoCompletoResponse response = 
    //         new SimuladoCompletoResponse(simulado, disciplinasResponse);

    //     LocalDateTime dataInicio = simulado.getDataInicio();
    //     response.setDatas(new DataResponse(
    //         dataInicio, this.calcularHoraFim(
    //             dataInicio, simulado.getDuracao())));

    //     return response;
    // }

    public LocalDateTime calcularHoraFim(UUID simuladoId) {
        Simulado simulado = simuladoRepository.findById(simuladoId).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + simuladoId));
        return FoxUtils.calcularHoraFimSimulado(
            simulado.getDataInicio(), simulado.getDuracao());
    }

    public void delete(UUID id) {
        // validar se já existem inscrições ou questoes para o simulado.
        simuladoRepository.deleteById(id);
    }

    public List<SimuladoResumoResponse> findAll() {
       
        List<SimuladoResumoResponse> result =
            new ArrayList<SimuladoResumoResponse>();

        String sql = """
                    select s.id, s.titulo, s.data_inicio, c.titulo as curso, s.duracao  
                    from simulados s inner join cursos c on c.id = s.curso_id 
                    order by s.data_inicio desc
                """;
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            LocalDateTime dataInicio = rs.getTimestamp(
                "data_inicio").toLocalDateTime();
            LocalDateTime dataFim = FoxUtils.calcularHoraFimSimulado(dataInicio, 
                rs.getString("duracao"));
            SimuladoResumoResponse obj = new SimuladoResumoResponse(
                UUID.fromString(rs.getString("id")),
                rs.getString("titulo"),
                rs.getString("curso"),
                dataInicio,
                dataFim);  

            result.add(obj);
            
            return obj;
        }); 

        return result;
    }

    public UUID getCursoAssociado(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Simulado não encontrado: " + id));
        return simulado.getCursoId();
    }

    public List<Simulado> findByCursoId(UUID cursoId) {
        return simuladoRepository.findByCursoId(cursoId);
    }

    public Boolean existsByCursoId(UUID cursoId) {
        return this.simuladoRepository.existsByCursoId(cursoId);
    }

    public List<SimuladoResumoResponse> findByExample(String filter) throws Exception {
       
        List<SimuladoResumoResponse> result =
            new ArrayList<SimuladoResumoResponse>();           

        Simulado simulado = FoxUtils.criarObjetoDinamico(filter, Simulado.class);

        String sql = """
                select s.id, s.titulo, s.data_inicio, c.titulo as curso, s.duracao  
                from simulados s inner join cursos c on c.id = s.curso_id where 1=1 
            """;

        if (simulado.getId() != null)
            sql += " and s.id = '" + simulado.getId() + "' ";
        if (simulado.getTitulo() != null)
            sql += " and s.titulo ilike '%" + simulado.getTitulo() + "%' ";
        if (simulado.getDescricao() != null)
            sql += " and s.descricao ilike '%" + simulado.getDescricao() + "%' ";
        if (simulado.getAlternativasPorQuestao() != null)
            sql += " and s.alternativas_por_questao = " + simulado.getAlternativasPorQuestao() + " ";
        if (simulado.getQuantidadeQuestoes() != null)
            sql += " and s.quantidade_questoes = " + simulado.getQuantidadeQuestoes() + " ";
        if (simulado.getDataInicio() != null)
            sql += " and s.data_inicio = '" + simulado.getDataInicio() + "' ";
        if (simulado.getValor() != null)
            sql += " and s.valor = " + simulado.getValor() + " ";

        sql += " order by s.data_inicio desc";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            
            LocalDateTime dataInicio = rs.getTimestamp(
                "data_inicio").toLocalDateTime();
            
            LocalDateTime dataFim = FoxUtils.calcularHoraFimSimulado(dataInicio, 
                rs.getString("duracao"));

            SimuladoResumoResponse obj = new SimuladoResumoResponse(
                UUID.fromString(rs.getString("id")),
                rs.getString("titulo"),
                rs.getString("curso"),
                dataInicio,
                dataFim);

            result.add(obj);
            
            return obj;
        }); 

        return result;
    }

    public int obterQuantidadeQuestoes(UUID id) {
        return simuladoRepository.obterQuantidadeQuestoes(id);
    }

    public boolean isExpirado(UUID simuladoId) {
        LocalDateTime horaAtual = LocalDateTime.now();
        return this.simuladoRepository.simuladosExpirados(simuladoId, horaAtual).size() > 0;
    }

}
