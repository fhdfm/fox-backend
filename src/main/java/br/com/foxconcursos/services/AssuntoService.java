package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.dto.AssuntoResponse;
import br.com.foxconcursos.repositories.AssuntoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssuntoService {

    private final AssuntoRepository assuntoRepository;
    private final JdbcTemplate jdbcTemplate;

    public AssuntoService(AssuntoRepository assuntoRepository, JdbcTemplate jdbcTemplate) {
        this.assuntoRepository = assuntoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Assunto salvar(Assunto assunto) {
        if (assunto.getNome() == null || assunto.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do assunto.");

        if (assunto.getDisciplinaId() == null)
            throw new IllegalArgumentException("Informe a disciplina.");

        UUID id = assunto.getId();

        if (id == null) {
            if (assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), assunto.getDisciplinaId()))
                throw new IllegalArgumentException("Assunto já cadastrado.");
            return assuntoRepository.save(assunto);
        }

        Assunto assuntoDB = assuntoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assunto não encontrado."));

        if (!assuntoDB.getNome().equals(assunto.getNome())
                && assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), assunto.getDisciplinaId()))
            throw new IllegalArgumentException("Assunto já cadastrado.");

        assuntoDB.setNome(assunto.getNome());
        assuntoDB.setDisciplinaId(assunto.getDisciplinaId());

        return assuntoRepository.save(assunto);
    }

    public List<AssuntoResponse> findAll(Assunto filter) throws Exception {
        List<AssuntoResponse> result =
                new ArrayList<>();
        String sql = """
                select a.id, a.nome, d.nome as disciplina from assunto a
                join disciplinas d on d.id = a.disciplina_id
                where 1 = 1
                """;
        if (filter != null) {
            if (filter.getId() != null)
                sql += " and a.id = '" + filter.getId() + "' ";
            if (filter.getDisciplinaId().toString() != null)
                sql += " and d.id = '" + filter.getDisciplinaId() + "' ";
        }
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            AssuntoResponse obj = new AssuntoResponse(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("nome"),
                    rs.getString("disciplina")
            );
            result.add(obj);
            return obj;
        });
        return result;
    }


    public List<Assunto> findAll() {
        return assuntoRepository.findAll();
    }

    public void deletar(UUID assuntoId) {
        assuntoRepository.deleteById(assuntoId);
    }

    public Assunto findById(UUID assuntoId) {
        return assuntoRepository.findById(assuntoId)
                .orElseThrow(() -> new IllegalArgumentException("Assunto não encontrado."));
    }

}
