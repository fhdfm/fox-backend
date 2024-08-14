package br.com.foxconcursos.services;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Comentario;
import br.com.foxconcursos.domain.PerfilUsuario;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.ComentarioRequest;
import br.com.foxconcursos.dto.ComentarioResponse;
import br.com.foxconcursos.repositories.ComentarioRepository;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class ComentarioService {
    
    private final ComentarioRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public ComentarioService(ComentarioRepository repository, 
        JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(ComentarioRequest request, UUID questaoId) {
        
        if (request.getDescricao() == null || request.getDescricao().trim().isEmpty())
            throw new IllegalArgumentException("Comentário não deve ser vazio.");

        Comentario comentario = new Comentario(request);
        comentario.setQuestaoId(questaoId);

        UsuarioLogado usuarioLogado = SecurityUtil.obterUsuarioLogado();
        UUID usuarioId = usuarioLogado.getId();

        comentario.setUsuarioId(usuarioId);

        this.repository.save(comentario);
    }

    public void delete(UUID comentarioId) {
        
        UsuarioLogado user = SecurityUtil.obterUsuarioLogado();
        
        if (user.getPerfil() == PerfilUsuario.ADMIN) {
            this.repository.deleteById(comentarioId);
        } else {
            Comentario comentario = this.repository.findById(
                comentarioId).orElseThrow();
            if (comentario.getUsuarioId().equals(user.getId())) {
                this.repository.deleteById(comentarioId);
            } else {
                throw new IllegalArgumentException(
                    "Somente o autor pode excluir o comentário.");
            }
        }
    }

    public List<ComentarioResponse> findByQuestaoId(UUID questaoId) {

        String sql = """
                select u.id as usuarioId, u.nome, c.id as comentarioId, c.data, c.descricao from comentarios c
                inner join usuarios u on u.id = c.usuario_id and c.questao_id = ?
                order by c.data desc
            """;

        return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            ComentarioResponse response = new ComentarioResponse();
            response.setUsuarioId(
                UUID.fromString(rs.getString("usuarioId")));
            response.setComentarioId(
                UUID.fromString(rs.getString("comentarioId")));
            response.setUsuario(rs.getString("nome"));
            response.setDescricao(rs.getString("descricao"));
            response.setData(FoxUtils.convertLocalDateTimeToDate(
                rs.getTimestamp("data").toLocalDateTime()));
            return response;
        }, questaoId);

    }

}
