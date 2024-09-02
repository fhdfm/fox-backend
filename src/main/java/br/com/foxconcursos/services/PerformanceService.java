package br.com.foxconcursos.services;

import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Performance;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.DashboardResponse;
import br.com.foxconcursos.dto.PerformanceResponse;
import br.com.foxconcursos.events.PerformanceEvent;
import br.com.foxconcursos.repositories.PerformanceRepository;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final JdbcTemplate jdbcTemplate;

    public PerformanceService(PerformanceRepository performanceRepository,
                              JdbcTemplate jdbcTemplate) {
        this.performanceRepository = performanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PerformanceResponse obterPerformance() {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        String sql = """
                    select sum(acertos) as acertos, sum(erros) as erros from performance where usuario_id = ?;    
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new PerformanceResponse(rs.getInt("acertos"),
                    rs.getInt("erros"));
        }, usuarioLogado.getId());
    }

    public PerformanceResponse obterPerformanceMesAno(int mes, int ano) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        String sql = """
                    select sum(acertos) as acertos, sum(erros) as erros from performance where usuario_id = ?
                    and mes = ? and ano = ?;    
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new PerformanceResponse(rs.getInt("acertos"),
                    rs.getInt("erros"));
        }, usuarioLogado.getId(), mes, ano);
    }

    public PerformanceResponse obterPerformanceIntervalo(int mesInicio, int anoInicio, int mesFim, int anoFim) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        String sql = """
                    SELECT sum(acertos) as acertos, sum(erros) as erros
                    FROM performance
                    WHERE 
                        usuarioId = ? AND (
                            (ano = ? AND mes >= ?)
                            OR (ano = ? AND mes <= ?)
                            OR (ano > ? AND ano < ?)
                        );
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                    return new PerformanceResponse(rs.getInt("acertos"),
                            rs.getInt("erros"));
                }, usuarioLogado.getId(), anoInicio, mesInicio, anoFim,
                mesFim, anoInicio, anoFim);
    }

    public PerformanceResponse obterPerformanceAno(int ano) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        String sql = """
                    select sum(acertos) as acertos, sum(erros) as erros from performance where usuario_id = ?
                    and ano = ?;    
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new PerformanceResponse(rs.getInt("acertos"),
                    rs.getInt("erros"));
        }, usuarioLogado.getId(), ano);
    }

    @EventListener
    public void handlePerformanceEvent(PerformanceEvent event) {

        Performance performance =
                this.performanceRepository.findByUsuarioIdAndMesAndAnoAndDisciplinaId(
                        event.getUsuarioId(), event.getMes(), event.getAno(), event.getDisciplinaId());

        if (performance == null) {
            performance = new Performance();
            performance.setUsuarioId(event.getUsuarioId());
            performance.setMes(event.getMes());
            performance.setAno(event.getAno());
            performance.setDisciplinaId(event.getDisciplinaId());

            if (event.isRepostaCorreta())
                performance.setAcertos(performance.getAcertos() + 1);
            else
                performance.setErros(performance.getErros() + 1);

            this.performanceRepository.save(performance);

        } else {

            if (event.isRepostaCorreta())
                jdbcTemplate.update("UPDATE performance SET acertos = acertos + 1 WHERE id = ?", performance.getId());
            else
                jdbcTemplate.update("UPDATE performance SET erros = erros + 1 WHERE id = ?", performance.getId());
        }
    }

    public DashboardResponse obterDadosDash() {
        DashboardResponse response = new DashboardResponse();

        jdbcTemplate.query(
                "SELECT COUNT(*) AS total FROM usuarios WHERE perfil != 'ADMIN'",
                (rs) -> {
                    response.setAlunos(rs.getInt("total"));
                }
        );

        jdbcTemplate.query(
                "SELECT COUNT(*) AS total FROM questoes",
                (rs) -> {
                    response.setQuestoes(rs.getInt("total"));
                }
        );

        jdbcTemplate.query(
                "SELECT COUNT(*) AS total FROM cursos",
                (rs) -> {
                    response.setCursos(rs.getInt("total"));
                }
        );

        jdbcTemplate.query(
                "SELECT COUNT(*) AS total FROM simulados",
                (rs) -> {
                    response.setSimulados(rs.getInt("total"));
                }
        );

        return response;
    }
}
