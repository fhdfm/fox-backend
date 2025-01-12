package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.AulaResponse;
import br.com.foxconcursos.dto.CursoAlunoResponse;
import br.com.foxconcursos.dto.DisciplinaAlunoResponse;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public CursoAlunoResponse obterCurso(UUID cursoId) throws IOException {
        return obterCurso(cursoId, null);
    }

    public CursoAlunoResponse obterCurso(UUID cursoId, UUID aulaId) throws IOException {

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        if (!produtoService.estaMatriculado(cursoId, usuarioId))
            throw new IllegalStateException("O aluno: " + usuarioId
                    + " não esta matriculado no curso: " + cursoId);


        String sqlInicio = """
                SELECT DISTINCT 
                    c.id AS cursoId, 
                    c.titulo AS cursoNome, 
                    d.id AS disciplinaId, 
                    d.nome AS disciplinaNome 
                FROM cursos c
                INNER JOIN curso_disciplina cd ON c.id = cd.curso_id
                INNER JOIN disciplinas d ON cd.disciplina_id = d.id
                WHERE c.id = ?
                  AND EXISTS (
                      SELECT 1 
                      FROM aulas a
                      INNER JOIN aula_conteudo ac ON a.id = ac.aula_id
                      WHERE a.disciplina_id = d.id
                        AND ac.tipo = 'VIDEO'
                  )
                ORDER BY d.nome;
                """;

        CursoAlunoResponse response = new CursoAlunoResponse();

        List<DisciplinaAlunoResponse> disciplinas = new ArrayList<>();

        jdbcTemplate.query(sqlInicio, (rs) -> {
            response.setId(rs.getObject("cursoId", UUID.class));
            response.setNome(rs.getString("cursoNome"));

            DisciplinaAlunoResponse disciplinaResponse = new DisciplinaAlunoResponse();
            disciplinaResponse.setId(rs.getObject("disciplinaId", UUID.class));
            disciplinaResponse.setNome(rs.getString("disciplinaNome"));
            disciplinas.add(disciplinaResponse);
        }, cursoId);

        if (disciplinas.isEmpty()) {
            throw new IllegalStateException("Nenhuma disciplina encontrada para o curso: " + cursoId);
        }

        String sqlAulas = """
                    SELECT 
                        a.id AS id,
                        a.titulo AS titulo,
                        (p.aula_id IS NOT NULL) AS finalizada
                    FROM aulas a
                    LEFT JOIN progresso p ON a.id = p.aula_id AND p.usuario_id = ?
                    WHERE a.curso_id = ? 
                    AND a.disciplina_id = ?
                    ORDER BY a.titulo ASC;        
                """;

        for (DisciplinaAlunoResponse dr : disciplinas) {

            List<AulaResponse> aulasDisciplina = new ArrayList<>();

            jdbcTemplate.query(sqlAulas, (rs) -> {

                AulaResponse aulaResponse = new AulaResponse();
                aulaResponse.setTitulo(rs.getString("titulo"));
                aulaResponse.setId(rs.getObject("id", UUID.class));
                aulaResponse.setFinalizada(rs.getBoolean("finalizada"));

                aulasDisciplina.add(aulaResponse);

            }, usuarioId, cursoId, dr.getId());

            dr.setAulas(aulasDisciplina);
        }

        response.setDisciplinas(disciplinas);

        List<AulaResponse> aulasPrimeiraDisciplina = disciplinas.get(0).getAulas();
        if (aulasPrimeiraDisciplina.isEmpty()) {
            throw new IllegalStateException("Nenhuma aula encontrada para a primeira disciplina do curso: " + cursoId);
        }

        if (aulaId == null)
            aulaId = aulasPrimeiraDisciplina.get(0).getId();

        response.setAula(obterAula(aulaId));

        return response;
    }

    private AulaResponse obterAula(UUID aulaId) throws IOException {
        return this.aulaService.buscarPorId(aulaId);
    }

    public void validarDownload(UUID cursoId, String fileId) {

        UUID cursoDbId = this.aulaService.findCursoIdByFileId(fileId);
        if (cursoDbId != null && !cursoDbId.equals(cursoDbId))
            throw new IllegalStateException("Acesso Negado.");

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();

        if (!this.produtoService.estaMatriculado(cursoId, usuarioLogado.getId()))
            throw new IllegalStateException("Acesso Negado.");
    }

    public String obterKeyFromFileId(UUID fileId) {

        String sql = "select key from aula_conteudo where file_id = ?";

        return jdbcTemplate.query(sql, (rs) -> {
            return rs.getString("key");
        }, fileId);

    }

}
