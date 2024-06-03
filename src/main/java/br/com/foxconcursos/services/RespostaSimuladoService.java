package br.com.foxconcursos.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.RespostaSimulado;
import br.com.foxconcursos.domain.RespostaSimuladoQuestao;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.StatusSimulado;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.DisciplinaQuestoesResponse;
import br.com.foxconcursos.dto.ItemQuestaoResponse;
import br.com.foxconcursos.dto.QuestaoSimuladoResponse;
import br.com.foxconcursos.dto.RankingSimuladoResponse;
import br.com.foxconcursos.dto.RespostaSimuladoRequest;
import br.com.foxconcursos.dto.ResultadoSimuladoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.repositories.RespostaQuestaoSimuladoRepository;
import br.com.foxconcursos.repositories.RespostaSimuladoRepository;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class RespostaSimuladoService {
    
    private SimuladoService simuladoService;
    private final RespostaSimuladoRepository respostaSimuladoRepository;
    private final RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository;
    private final UsuarioServiceImpl usuarioService;
    private final ItemQuestaoSimuladoService itemQuestaoSimuladoService;
    private final JdbcTemplate jdbcTemplate;

    public RespostaSimuladoService(RespostaSimuladoRepository respostaSimuladoRepository, 
        RespostaQuestaoSimuladoRepository respostaQuestaoSimuladoRepository,
        UsuarioServiceImpl usuarioService, SimuladoService simuladoService, 
        ItemQuestaoSimuladoService itemQuestaoSimuladoService, 
        JdbcTemplate jdbcTemplate) {
        
        this.usuarioService = usuarioService;
        this.respostaSimuladoRepository = respostaSimuladoRepository;
        this.respostaQuestaoSimuladoRepository = respostaQuestaoSimuladoRepository;
        this.simuladoService = simuladoService;
        this.itemQuestaoSimuladoService = itemQuestaoSimuladoService;
        this.jdbcTemplate = jdbcTemplate;

    }

    public UUID iniciar(UUID simuladoId, String login) {
        
        UsuarioLogado user =
            usuarioService.loadUserByUsername(login);

        Optional<RespostaSimulado> respostaDB = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());
        
        if (respostaDB.isPresent())
            return respostaDB.get().getId();

        RespostaSimulado resposta = new RespostaSimulado();
        resposta.setUsuarioId(user.getId());
        resposta.setSimuladoId(simuladoId);
        resposta.setDataInicio(LocalDateTime.now());
        resposta.setStatus(StatusSimulado.EM_ANDAMENTO);
        resposta = this.respostaSimuladoRepository.save(resposta);

        return resposta.getId();
    }

    public StatusSimulado obterStatus(UUID simuladoId, String login) {

        UsuarioLogado user = usuarioService.loadUserByUsername(login);

        Optional<RespostaSimulado> respostaSimulado = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());
        
        if (respostaSimulado.isEmpty())
            return StatusSimulado.NAO_INICIADO;
        
        return respostaSimulado.get().getStatus();
    }

    @Transactional
    public UUID salvar(UUID simuladoId, String login, 
        RespostaSimuladoRequest resposta) {
        
        UsuarioLogado user =
            this.usuarioService.loadUserByUsername(login);
        
        Optional<RespostaSimulado> respostaSimulado = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
                simuladoId, user.getId());
        
        UUID respostaSimuladoId = respostaSimulado.isPresent() ? 
            respostaSimulado.get().getId() : null;

        System.out.println("respostaSimuladoId: " + respostaSimuladoId);

        if (respostaSimuladoId == null) {
            respostaSimuladoId = iniciar(simuladoId, login);
        }

        System.out.println("respostaSimuladoId: " + respostaSimuladoId);
        
        List<RespostaSimuladoQuestao> respostaDB =
            this.respostaQuestaoSimuladoRepository.findByRespostaSimuladoIdAndQuestaoId(
            respostaSimuladoId, resposta.getQuestaoId());

        Boolean acertou = itemQuestaoSimuladoService.estaCorreta(
            resposta.getItemQuestaoId(), resposta.getQuestaoId());

        RespostaSimuladoQuestao respostaQuestao = null;

        if (respostaDB.size() == 1) {
            respostaQuestao = respostaDB.get(0);
            respostaQuestao.setCorreta(acertou);
            respostaQuestao.setItemQuestaoId(resposta.getItemQuestaoId());
            return respostaQuestaoSimuladoRepository.save(respostaQuestao).getId();
        } else if (respostaDB.size() > 1) {
            System.out.println("O simulado possui mais de uma resposta para a mesma questão.");
            System.out.println("Simulado: " + simuladoId);
            System.out.println("Usuário: " + user.getId());
            System.out.println("Questão: " + resposta.getQuestaoId());
            System.out.println("Respostas: " + respostaDB.size());
            System.out.println("respostaSimuladoId: " + respostaSimuladoId);

            respostaQuestao = respostaDB.get(0);
            respostaQuestao.setCorreta(acertou);
            respostaQuestao.setItemQuestaoId(resposta.getItemQuestaoId());
            respostaQuestao = respostaQuestaoSimuladoRepository.save(respostaQuestao);

            for (int i = 1; i < respostaDB.size(); i++) {
                respostaQuestaoSimuladoRepository.delete(respostaDB.get(i));
            }

            return respostaQuestao.getId();
        } else {
            respostaQuestao = 
                new RespostaSimuladoQuestao(
                    respostaSimuladoId, resposta.getQuestaoId(), 
                    resposta.getItemQuestaoId(), acertou);
            return respostaQuestaoSimuladoRepository.save(respostaQuestao).getId();
        }
    }

    @Transactional
    public void finalizarViaJob(UUID simuladoId) {
        
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

        List<UUID> alunos = obterAlunosQueNaoIniciarmOSimulado(simuladoId);
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

    private List<UUID> obterAlunosQueNaoIniciarmOSimulado(UUID simuladoId) {
        
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
                s.id = :simuladoId
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
                s.id = :simuladoId
                AND rs.id IS NULL;
            """;

         List<UUID> alunos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return UUID.fromString(rs.getString("usuario_id"));
        }, simuladoId);

        return alunos;
    }

    @Transactional
    public UUID finalizar(UUID simuladoId, String login) {
        
        LocalDateTime horaFim = LocalDateTime.now();
        
        Simulado simulado = simuladoService.obterPorId(simuladoId);
        estaFinalizandoAposHorario(simulado.getDataInicio(), 
            simulado.getDuracao(), horaFim);

        UsuarioLogado user =
            usuarioService.loadUserByUsername(login);
        
        Optional<RespostaSimulado> respostaSimulado =
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioId(
            simuladoId, user.getId());

        if (respostaSimulado.isEmpty())
            throw new IllegalArgumentException("Simulado não iniciado.");
        
        // for (RespostaSimuladoRequest resposta : respostas) {

        //     Boolean acertou = itemQuestaoSimuladoService.estaCorreta(
        //         resposta.getItemQuestaoId(), resposta.getQuestaoId());         

        //     RespostaSimuladoQuestao respostaQuestao = 
        //         respostaQuestaoSimuladoRepository.findByRespostaSimuladoIdAndQuestaoId(
        //             respostaSimulado.getId(), resposta.getQuestaoId());
            
        //     if (respostaQuestao == null) {
        //         respostaQuestao = new RespostaSimuladoQuestao(
        //             respostaSimulado.getId(), resposta.getQuestaoId(), 
        //             resposta.getItemQuestaoId(), acertou); 
        //     } else {
        //         respostaQuestao.setCorreta(acertou);
        //         respostaQuestao.setItemQuestaoId(resposta.getItemQuestaoId());
        //     }

        //     respostaQuestaoSimuladoRepository.save(respostaQuestao);
        // }
        
        respostaSimulado.get().setAcertos(0);
        respostaSimulado.get().setAcertosUltimas15(0);
        respostaSimulado.get().setDataFim(horaFim);
        respostaSimulado.get().setStatus(StatusSimulado.FINALIZADO);

        this.respostaSimuladoRepository.save(respostaSimulado.get());

        contabilizar(simuladoId, user.getId());

        return respostaSimulado.get().getId();
    }

    

    private void estaFinalizandoAposHorario(
        LocalDateTime dataInicio, String duracao, LocalDateTime horarioEnvio) {
        
        LocalDateTime horarioFim = simuladoService.calcularHoraFim(dataInicio, duracao);
        if (horarioEnvio.isAfter(horarioFim))
            throw new IllegalArgumentException("Simulado finalizado após o horário limite.");
    }

    public SimuladoCompletoResponse obterRespostas(
        SimuladoCompletoResponse simulado, String login) {

        UsuarioLogado usuarioLogado =
            this.usuarioService.loadUserByUsername(login);
        
        Optional<RespostaSimulado> respostaSimulado = this.respostaSimuladoRepository
            .findBySimuladoIdAndUsuarioId(simulado.getId(), usuarioLogado.getId());
        
        if (respostaSimulado.isPresent()) {
            for (DisciplinaQuestoesResponse disciplinas : simulado.getDisciplinas()) {
                preencherQuestoesAluno(disciplinas.getQuestoes(), respostaSimulado.get().getId());
            }
        }

        return simulado;
    }

    private void preencherQuestoesAluno(List<QuestaoSimuladoResponse> questoes, UUID respostaId) {
        for (QuestaoSimuladoResponse questao : questoes) {
            List<RespostaSimuladoQuestao> resposta = 
                respostaQuestaoSimuladoRepository.findByRespostaSimuladoIdAndQuestaoId(
                    respostaId, questao.getId());
            if (resposta.size() == 1)
                preencherItem(questao.getAlternativas(), 
                resposta.get(0).getItemQuestaoId());
        }
    }

    private void preencherItem(List<ItemQuestaoResponse> alternativas, UUID id) {
        for (ItemQuestaoResponse alternativa : alternativas) {
            if (id.equals(alternativa.getId())) {
                alternativa.setItemMarcado(true);
                break;
            }
        }
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

    private void contabilizar(UUID simuladoId, UUID usuarioId) {
        
        RespostaSimulado resposta = 
            this.respostaSimuladoRepository.findBySimuladoIdAndUsuarioIdAndStatus(
                simuladoId, usuarioId, StatusSimulado.FINALIZADO);
        
        int totalQuestoes = simuladoService.obterQuantidadeQuestoes(simuladoId);
        int corte15Ultimas = totalQuestoes - 15;

        int acertos = 0;
        int acertosUltimas15 = 0;

        int contador = 1;
        
        for (RespostaSimuladoQuestao respostaQuestao : 
            respostaQuestaoSimuladoRepository.findByRespostaSimuladoId(
                resposta.getId())) {

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
                select u.nome, r.acertos from respostas_simulado r 
                inner join usuarios u on u.id = r.usuario_id where simulado_id = ? 
                order by acertos desc, acertos_ultimas_15 desc 
                """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            RankingSimuladoResponse rsr = new RankingSimuladoResponse();
            rsr.setNome(rs.getString("nome"));
            rsr.setAcertos(rs.getInt("acertos"));
            rsr.setClassificacao(rowNum + 1);
            ranking.add(rsr);

            return rsr;
        }, 
        simuladoId);

        response.setRanking(ranking);

        return response;
    }

    public ResultadoSimuladoResponse obterRanking(UUID simuladoId, String login) {
        
        UsuarioLogado usuarioLogado = 
            this.usuarioService.loadUserByUsername(login);
        
        ResultadoSimuladoResponse response = new ResultadoSimuladoResponse();

        Simulado simulado = simuladoService.obterPorId(simuladoId);

        response.setNome(simulado.getTitulo());
        response.setData(
            FoxUtils.convertLocalDateTimeToDate(
                simulado.getDataInicio()));
        
        List<RankingSimuladoResponse> ranking = new ArrayList<RankingSimuladoResponse>();

        String sql = """
                select u.nome, r.acertos from respostas_simulado r 
                inner join usuarios u on u.id = r.usuario_id where simulado_id = ? 
                and r.usuario_id = ? order by acertos desc, acertos_ultimas_15 desc 
                """;

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            RankingSimuladoResponse rsr = new RankingSimuladoResponse();
            rsr.setNome(rs.getString("nome"));
            rsr.setAcertos(rs.getInt("acertos"));
            ranking.add(rsr);

            return rsr;
        }, 
        simuladoId,
        usuarioLogado.getId());

        response.setRanking(ranking);

        return response;
    }

    public List<UUID> recuperarSimuladosNaoFinalizados(LocalDateTime horaAtual) {
        return this.respostaSimuladoRepository
            .recuperarSimuladosNaoFinalizados(horaAtual);
    }
}
