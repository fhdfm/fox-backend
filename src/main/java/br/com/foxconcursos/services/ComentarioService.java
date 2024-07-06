package br.com.foxconcursos.services;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Comentario;
import br.com.foxconcursos.dto.ComentarioRequest;
import br.com.foxconcursos.dto.ComentarioResponse;
import br.com.foxconcursos.repositories.ComentarioRepository;
import br.com.foxconcursos.util.FoxUtils;

@Service
public class ComentarioService {
    
    private final ComentarioRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final AuthenticationService authenticationService;

    public ComentarioService(ComentarioRepository repository, 
        JdbcTemplate jdbcTemplate, AuthenticationService authenticationService) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.authenticationService = authenticationService;
    }

    public void save(ComentarioRequest request, UUID questaoId) {
        
        if (request.getDescricao() == null || request.getDescricao().trim().isEmpty())
            throw new IllegalArgumentException("Comentário não deve ser vazio.");

        Comentario comentario = new Comentario(request);
        comentario.setQuestaoId(questaoId);

        UUID usuarioId = this.authenticationService.obterUsuarioLogado();
        comentario.setUsuarioId(usuarioId);

        this.repository.save(comentario);
    }

    public List<ComentarioResponse> findByQuestaoId(UUID questaoId) {

        String sql = """
                select u.usuario_id, u.nome, c.data, c.descricao from comentarios c
                inner join usuarios u on u.id = c.usuario_id and c.questao_id = ?
                order by c.data desc
            """;

        return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            ComentarioResponse response = new ComentarioResponse();
            response.setUsuarioId(
                UUID.fromString(rs.getString("usuario_id")));
            response.setUsuario(rs.getString("nome"));
            response.setDescricao(rs.getString("descricao"));
            response.setData(FoxUtils.convertLocalDateTimeToDate(
                rs.getTimestamp("data").toLocalDateTime()));
            return response;
        }, questaoId);

    }

}
