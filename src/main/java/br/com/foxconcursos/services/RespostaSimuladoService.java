package br.com.foxconcursos.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.RespostaSimulado;
import br.com.foxconcursos.domain.RespostaSimuladoQuestao;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.ItemQuestaoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.RankingSimuladoResponse;
import br.com.foxconcursos.dto.RespostaSimuladoRequest;
import br.com.foxconcursos.dto.ResultadoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.events.RecalcularEvent;
import br.com.foxconcursos.repositories.RespostaQuestaoSimuladoRepository;
import br.com.foxconcursos.repositories.RespostaSimuladoRepository;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class RespostaSimuladoService {
    
    private Logger logger = LoggerFactory.getLogger(RespostaSimuladoService.class);

    private SimuladoService simuladoService;
    private final RespostaSimuladoRepository respostaSimuladoRepository;
    private final RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository;
    private final JdbcTemplate jdbcTemplate;

    public RespostaSimuladoService(RespostaSimuladoRepository respostaSimuladoRepository, 
        RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository,
        SimuladoService simuladoService, 
        JdbcTemplate jdbcTemplate) {
        
        this.respostaSimuladoRepository = respostaSimuladoRepository;
        this.respostaQuestaoSimuladoRepository = respostaQuestaoSimuladoRepository;
        this.simuladoService = simuladoService;
        this.jdbcTemplate = jdbcTemplate;

    }

    @Transactional
    public UUID iniciar(UUID simuladoId) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        UUID usuarioId = usuarioLogado.getId();

        Optional<RespostaSimulado> respostaDB = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, usuarioId);
        
        if (respostaDB.isPresent())
            return respostaDB.get().getId();

        try {
            
            RespostaSimulado resposta = new RespostaSimulado();
            resposta.setUsuarioId(usuarioId);
            resposta.setSimuladoId(simuladoId);
            resposta.setDataInicio(LocalDateTime.now());
            resposta.setStatus(StatusSimulado.EM_ANDAMENTO);
            resposta = this.respostaSimuladoRepository.save(resposta);

            return resposta.getId();
        
        } catch (DataIntegrityViolationException e) {
            
            respostaDB = this.respostaSimuladoRepository
                .findBySimuladoIdAndUsuarioId(simuladoId, usuarioId);
            
            if (respostaDB.isPresent())
                return respostaDB.get().getId();

            throw new IllegalStateException("Erro ao iniciar o simulado: " 
                + e.getMessage(), e);
        }
    }

    @Transactional
    public StatusSimulado obterStatus(UUID simuladoId) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        if (this.simuladoService.isExpirado(simuladoId)) {
            return StatusSimulado.FINALIZADO;
        } else {
            Optional<RespostaSimulado> respostaSimulado =
                    this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                            simuladoId, usuarioId);

            if (respostaSimulado.isEmpty())
                return StatusSimulado.NAO_INICIADO;
            return respostaSimulado.get().getStatus();
        }
    }

    @Transactional
    public UUID salvar(UUID simuladoId, RespostaSimuladoRequest resposta) 
        throws SQLException {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();            

        String findBySimuladoIdAndUsuarioId = """
                select id, version from respostas_simulado where simulado_id = ?
                and usuario_id = ? and status = 'EM_ANDAMENTO';                
        """;
                
        RespostaSimulado respostaSimulado = jdbcTemplate.queryForObject(findBySimuladoIdAndUsuarioId, 
            (rs, rowNum) ->  {
                RespostaSimulado resp = new RespostaSimulado();
                resp.setId(UUID.fromString(rs.getString("id")));
                resp.setVersion(rs.getInt("version"));
                return resp;
            }, simuladoId, usuarioId);

        UUID respostaSimuladoId = respostaSimulado != null 
            ? respostaSimulado.getId() : null;

        if (respostaSimuladoId == null) {
            throw new IllegalArgumentException("Não é possível salvar um simulado "
            + "que ainda não foi iniciado.");
        }

        String findByRespostaSimuladoIdAndQuestaoId = """
                select rsq.* from respostas_simulado_questao rsq inner join 
                respostas_simulado rs on rsq.resposta_simulado_id = rs.id
                where rsq.resposta_simulado_id = ? and rsq.questao_id = ? and rs.usuario_id = ?
        """;

        List<RespostaSimuladoQuestao> respostaDB = jdbcTemplate.query(
            findByRespostaSimuladoIdAndQuestaoId, (rs, rowNum) -> {
                RespostaSimuladoQuestao rsq = new RespostaSimuladoQuestao();
                rsq.setId(UUID.fromString(rs.getString("id")));
                rsq.setRespostaSimuladoId(UUID.fromString(
                    rs.getString("resposta_simulado_id")));
                rsq.setQuestaoId(UUID.fromString(rs.getString("questao_id")));
                rsq.setItemQuestaoId(UUID.fromString(rs.getString("item_questao_id")));
                rsq.setVersion(rs.getInt("version"));
                return rsq;
            }, respostaSimuladoId, resposta.getQuestaoId(), usuarioId);

        String questaoEstahCorreta = """
            SELECT correta FROM itens_questao_simulado 
            WHERE id = ? AND questao_simulado_id = ?
        """;

        Boolean acertou = jdbcTemplate.queryForObject(questaoEstahCorreta, 
            (rs, rowNum) -> rs.getBoolean("correta"), 
            resposta.getItemQuestaoId(), resposta.getQuestaoId());

        if (respostaDB.isEmpty()) {
            
            logger.info("[insert] size: " + respostaDB.size() + " - [respostaSimuladoId:" + respostaSimuladoId 
                + "][questaoId:" + resposta.getQuestaoId() + "][itemQuestaoId:" 
                + resposta.getItemQuestaoId() + "][correta:" + acertou + "]");

            jdbcTemplate.update("""
                insert into respostas_simulado_questao 
                (resposta_simulado_id, questao_id, item_questao_id, correta) 
                values (?, ?, ?, ?)
                """, respostaSimuladoId, resposta.getQuestaoId(), 
                resposta.getItemQuestaoId(), acertou);
        } else {
            
            logger.info("[update] size: " + respostaDB.size() + " - [respostaSimuladoId:" + respostaSimuladoId 
                + "][questaoId:" + resposta.getQuestaoId() + "][itemQuestaoId:" 
                + resposta.getItemQuestaoId() + "][correta:" + acertou + "]");

            jdbcTemplate.update("""
                update respostas_simulado_questao 
                set item_questao_id = ?, correta = ? 
                where id = ?
                """, resposta.getItemQuestaoId(), acertou, 
                    respostaDB.get(0).getId());
        }

        return respostaSimuladoId;
    }

    @Transactional
    public void finalizarSimulado(UUID simuladoId) {
        
        List<RespostaSimulado> respostas = 
            this.respostaSimuladoRepository.findBySimuladoIdAndStatus(
                simuladoId, StatusSimulado.EM_ANDAMENTO);
        
        for (RespostaSimulado resposta : respostas) {
            
            resposta.setAcertos(0);
            resposta.setAcertosUltimas15(0);
            resposta.setDataFim(LocalDateTime.now());
            resposta.setStatus(StatusSimulado.FINALIZADO);
            this.respostaSimuladoRepository.save(resposta);            

            contabilizar(simuladoId, resposta.getUsuarioId());
        }

        finalizarViaJobNaoIniciados(simuladoId);
    }

    private void finalizarViaJobNaoIniciados(UUID simuladoId) {

        List<UUID> alunos = obterAlunosQueNaoIniciaramOSimulado(simuladoId);
        if (!alunos.isEmpty()) {
            for (UUID aluno : alunos) {
                RespostaSimulado resposta = new RespostaSimulado();
                resposta.setUsuarioId(aluno);
                resposta.setSimuladoId(simuladoId);
                resposta.setDataInicio(LocalDateTime.now());
                resposta.setDataFim(LocalDateTime.now());
                resposta.setStatus(StatusSimulado.FINALIZADO);
                resposta.setAcertos(0);
                resposta.setAcertosUltimas15(0);
                this.respostaSimuladoRepository.save(resposta);
            }
        }
    }

    private List<UUID> obterAlunosQueNaoIniciaramOSimulado(UUID simuladoId) {
        
        String sql = """
            SELECT 
                m.usuario_id
            FROM 
                matriculas m
            JOIN 
                cursos c ON m.produto_id = c.id AND m.tipo_produto = 'CURSO'
            JOIN 
                simulados s ON s.curso_id = c.id
            LEFT JOIN 
                respostas_simulado rs ON rs.simulado_id = s.id AND rs.usuario_id = m.usuario_id
            WHERE 
                s.id = ?
                AND rs.id IS NULL
            
            UNION
            
            SELECT 
                m.usuario_id
            FROM 
                matriculas m
            JOIN 
                simulados s ON m.produto_id = s.id AND m.tipo_produto = 'SIMULADO'
            LEFT JOIN 
                respostas_simulado rs ON rs.simulado_id = s.id AND rs.usuario_id = m.usuario_id
            WHERE 
                s.id = ?
                AND rs.id IS NULL;
            """;

         List<UUID> alunos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return UUID.fromString(rs.getString("usuario_id"));
        }, simuladoId, simuladoId);

        return alunos;
    }

    @Transactional
    public UUID finalizar(UUID simuladoId) {
        
        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();        

        LocalDateTime horaFim = LocalDateTime.now();
        
        Simulado simulado = simuladoService.obterPorId(simuladoId);
        estaFinalizandoAposHorario(simulado.getDataInicio(), 
            simulado.getDuracao(), horaFim);
        
        Optional<RespostaSimulado> respostaSimulado =
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
            simuladoId, usuarioId);

        if (respostaSimulado.isEmpty())
            throw new IllegalArgumentException("Simulado não iniciado.");
        
        respostaSimulado.get().setAcertos(0);
        respostaSimulado.get().setAcertosUltimas15(0);
        respostaSimulado.get().setDataFim(horaFim);
        respostaSimulado.get().setStatus(StatusSimulado.FINALIZADO);

        this.respostaSimuladoRepository.save(respostaSimulado.get());

        contabilizar(simuladoId, usuarioId);

        return respostaSimulado.get().getId();
    }

    private void estaFinalizandoAposHorario(
        LocalDateTime dataInicio, String duracao, LocalDateTime horarioEnvio) {
        
        LocalDateTime horarioFim = FoxUtils.calcularHoraFimSimulado(dataInicio, duracao);
        if (horarioEnvio.isAfter(horarioFim))
            throw new IllegalArgumentException("Simulado finalizado após o horário limite.");
    }

    @Transactional
    public SimuladoCompletoResponse obterRespostas(SimuladoCompletoResponse simulado) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        Optional<RespostaSimulado> respostaSimulado = this.respostaSimuladoRepository
            .findBySimuladoIdAndUsuarioId(simulado.getId(), usuarioId);

        if (respostaSimulado.isPresent()) {
            UUID respostaSimuladoId = respostaSimulado.get().getId();

            // Buscar todas as respostas de uma vez
            List<RespostaSimuladoQuestao> respostasSimuladoQuestao = respostaQuestaoSimuladoRepository
                .findByRespostaSimuladoId(respostaSimuladoId);

            Map<UUID, UUID> questaoParaItemMap = respostasSimuladoQuestao.stream()
                .collect(Collectors.toMap(RespostaSimuladoQuestao::getQuestaoId, 
                    RespostaSimuladoQuestao::getItemQuestaoId));

            simulado.getDisciplinas().parallelStream().forEach(disciplina -> 
                preencherQuestoesAluno(disciplina.getQuestoes(), questaoParaItemMap)
            );
        }

        return simulado;
    }

    private void preencherQuestoesAluno(List<QuestaoSimuladoResponse> questoes, 
        Map<UUID, UUID> questaoParaItemMap) {

        questoes.parallelStream().forEach(questao -> {
            UUID itemQuestaoId = questaoParaItemMap.get(questao.getId());
            if (itemQuestaoId != null) {
                preencherItem(questao.getAlternativas(), itemQuestaoId);
            }
        });

    }

    private void preencherItem(List<ItemQuestaoResponse> alternativas, UUID id) {
        alternativas.parallelStream().forEach(alternativa -> {
            if (id.equals(alternativa.getId())) {
                alternativa.setItemMarcado(true);
            }
        });
    }

    public void finalizar() {
        String sql = """
            select simulado_id, usuario_id from respostas_simulado 
            where status = 'EM_ANDAMENTO' and data_fim < now()
            """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            contabilizar(UUID.fromString(rs.getString("simulado_id")), 
                UUID.fromString(rs.getString("usuario_id")));
            return null;
        });
    }

    @EventListener
    void handleContabilizarEvent(RecalcularEvent event) {
        
        String sql = """
            select simulado_id, usuario_id from respostas_simulado 
            where status = 'FINALIZADO' and simulado_id = ?
            """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            
            UUID usuarioId = UUID.fromString(rs.getString("usuario_id"));
            contabilizar(event.getSimuladoId(), usuarioId);
            
            return null;

        }, event.getSimuladoId());
    }

    private void contabilizar(UUID simuladoId, UUID usuarioId) {
        
        RespostaSimulado resposta = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioIdAndStatus(
                simuladoId, usuarioId, StatusSimulado.FINALIZADO);
        
        int totalQuestoes = simuladoService.obterQuantidadeQuestoes(simuladoId);
        int corte15Ultimas = totalQuestoes - 15;

        List<RespostaSimuladoQuestao> respostasQuestoes = respostaQuestaoSimuladoRepository
            .findByRespostaSimuladoId(resposta.getId());

        int acertos = 0;
        int acertosUltimas15 = 0;

        int contador = 1;
        
        for (RespostaSimuladoQuestao respostaQuestao : respostasQuestoes) {
            if (respostaQuestao.isCorreta()) {
                acertos++;
                if (contador > corte15Ultimas)
                    acertosUltimas15++;
            }
            
            contador++;
        }

        resposta.setAcertos(acertos);
        resposta.setAcertosUltimas15(acertosUltimas15);
        
        this.respostaSimuladoRepository.save(resposta);
    }

    public ResultadoSimuladoResponse obterRanking(UUID simuladoId) {
        ResultadoSimuladoResponse response = new ResultadoSimuladoResponse();

        Simulado simulado = simuladoService.obterPorId(simuladoId);

        response.setNome(simulado.getTitulo());
        response.setData(
            FoxUtils.convertLocalDateTimeToDate(
                simulado.getDataInicio()));
        
        List<RankingSimuladoResponse> ranking = new ArrayList<RankingSimuladoResponse>();

        String sql = """
                select u.nome, u.cpf, r.acertos from respostas_simulado r 
                inner join usuarios u on u.id = r.usuario_id where simulado_id = ? 
                order by acertos desc, acertos_ultimas_15 desc 
                """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            RankingSimuladoResponse rsr = new RankingSimuladoResponse();
            rsr.setNome(rs.getString("nome"));
            rsr.setCpf(rs.getString("cpf"));
            rsr.setAcertos(rs.getInt("acertos"));
            rsr.setClassificacao(rowNum + 1);
            ranking.add(rsr);

            return rsr;
        }, 
        simuladoId);

        response.setRanking(ranking);

        return response;
    }

    public ResultadoSimuladoResponse obterRankingPorUsuario(UUID simuladoId) {
        
        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        ResultadoSimuladoResponse response = new ResultadoSimuladoResponse();

        Simulado simulado = simuladoService.obterPorId(simuladoId);

        response.setNome(simulado.getTitulo());
        response.setData(
            FoxUtils.convertLocalDateTimeToDate(
                simulado.getDataInicio()));
        
        List<RankingSimuladoResponse> ranking = new ArrayList<RankingSimuladoResponse>();

        String sql = """
                select u.nome, u.cpf, r.acertos from respostas_simulado r 
                inner join usuarios u on u.id = r.usuario_id where simulado_id = ? 
                and r.usuario_id = ? order by acertos desc, acertos_ultimas_15 desc 
                """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            RankingSimuladoResponse rsr = new RankingSimuladoResponse();
            rsr.setNome(rs.getString("nome"));
            rsr.setCpf(rs.getString("cpf"));
            rsr.setAcertos(rs.getInt("acertos"));
            ranking.add(rsr);

            return rsr;
        }, 
        simuladoId,
        usuarioId);

        response.setRanking(ranking);

        return response;
    }
}
