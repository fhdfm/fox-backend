package br.com.foxconcursos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.AulaResponse;
import br.com.foxconcursos.dto.CursoAlunoResponse;
import br.com.foxconcursos.dto.DisciplinaAlunoResponse;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class CursoAlunoService {
    
    private final JdbcTemplate jdbcTemplate;
    private final AulaService aulaService;
    private final ProdutoService produtoService;

    public CursoAlunoService(JdbcTemplate jdbcTemplate, AulaService aulaService, ProdutoService produtoService) {
        this.jdbcTemplate = jdbcTemplate;
        this.aulaService = aulaService;
        this.produtoService = produtoService;
    }

    public void salvarProgresso(UUID cursoId, UUID aulaId) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        String sql = "insert into progresso (usuario_id, curso_id, aula_id) values (?, ?, ?)";

        jdbcTemplate.update(sql, usuarioLogado.getId(), cursoId, aulaId);

    }

    public CursoAlunoResponse obterCurso(UUID cursoId) {
        return obterCurso(cursoId, null);
    }

    public CursoAlunoResponse obterCurso(UUID cursoId, UUID aulaId) {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        if (!produtoService.estaMatriculado(cursoId, usuarioId))
            throw new IllegalStateException("O aluno: " + usuarioId 
                    + " não esta matriculado no curso: " + cursoId);

        String sqlInicio = "select c.id as cursoId, c.titulo as cursoNome, d.id as disciplinaId, d.nome as disciplinaNome "
                        + "from cursos c inner join curso_disciplina cd on c.id = cd.curso_id " 
                        + "inner join disciplinas d on cd.disciplina_id = d.id " 
                        + "where c.id = ? " 
                        + "order by d.nome";
        
        CursoAlunoResponse response = new CursoAlunoResponse();
        
        List<DisciplinaAlunoResponse> disciplinas = new ArrayList<>();

        jdbcTemplate.query(sqlInicio, (rs) -> {
            response.setId(rs.getObject("cursoId",  UUID.class));
            response.setNome(rs.getString("cursoNome"));

            DisciplinaAlunoResponse disciplinaResponse = new DisciplinaAlunoResponse();
            disciplinaResponse.setId(rs.getObject("disciplinaId",  UUID.class));
            disciplinaResponse.setNome(rs.getString("disciplinaNome"));
            disciplinas.add(disciplinaResponse);
        }, cursoId);


        String sqlAulas = """
                SELECT 
                    a.id AS id,
                    a.titulo AS titulo,
                    ass.nome AS assunto,
                    (p.aula_id IS NOT NULL) AS finalizada
                FROM aulas a
                INNER JOIN assunto ass ON a.assunto_id = ass.id
                LEFT JOIN progresso p ON a.id = p.aula_id AND p.usuario_id = ?
                WHERE a.curso_id = ? 
                AND a.disciplina_id = ?
                ORDER BY a.ordem ASC;        
            """;

        for (DisciplinaAlunoResponse dr : disciplinas) {

            List<AulaResponse> aulasDisciplina = new ArrayList<>();

            jdbcTemplate.query(sqlAulas, (rs) -> {
            
                AulaResponse aulaResponse = new AulaResponse();
                aulaResponse.setAssunto(rs.getString("assunto"));
                aulaResponse.setTitulo(rs.getString("titulo"));
                aulaResponse.setId(rs.getObject("id", UUID.class));
                aulaResponse.setFinalizada(rs.getBoolean("finalizada"));

                aulasDisciplina.add(aulaResponse);

            }, usuarioId, cursoId, dr.getId());

            dr.setAulas(aulasDisciplina);
        }

        if (aulaId == null)
            aulaId = disciplinas.get(0).getAulas().get(0).getId();

        response.setAula(obterAula(aulaId));

        return response;
    }

    private AulaResponse obterAula(UUID aulaId) {
        return this.aulaService.buscarPorId(aulaId);
    }

}
