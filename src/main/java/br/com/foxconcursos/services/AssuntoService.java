package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.dto.AssuntoResponse;
import br.com.foxconcursos.repositories.AssuntoRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AssuntoService {

    private final AssuntoRepository assuntoRepository;
    private final JdbcTemplate jdbcTemplate;

    public AssuntoService(AssuntoRepository assuntoRepository, JdbcTemplate jdbcTemplate) {
        this.assuntoRepository = assuntoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Assunto salvar(UUID disciplinaId, Assunto assunto) {
        if (assunto.getNome() == null || assunto.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do assunto.");

        if (disciplinaId == null)
            throw new IllegalArgumentException("Informe a disciplina.");

        UUID id = assunto.getId();

        if (id == null) {
            if (assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), disciplinaId))
                throw new IllegalArgumentException("Assunto já cadastrado.");
            assunto.setDisciplinaId(disciplinaId);
            return assuntoRepository.save(assunto);
        }

        Assunto assuntoDB = assuntoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assunto não encontrado."));

        if (!assuntoDB.getNome().equals(assunto.getNome())
                && assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), disciplinaId))
            throw new IllegalArgumentException("Assunto já cadastrado.");

        assuntoDB.setNome(assunto.getNome());
        assuntoDB.setDisciplinaId(disciplinaId);

        return assuntoRepository.save(assunto);
    }

    public List<AssuntoResponse> findAll(Assunto filter) throws Exception {
        List<AssuntoResponse> result = new ArrayList<>();

        String sql = """
                select a.id, a.nome, d.nome as disciplina from assunto a
                join disciplinas d on d.id = a.disciplina_id
                where 1 = 1
                """;

        if (filter != null) {
            if (filter.getId() != null)
                sql += " and a.id = '" + filter.getId() + "' ";
            if (filter.getNome() != null)
                sql += " and upper(a.nome) like '%" + filter.getNome().toUpperCase() + "%' ";
            if (filter.getDisciplinaId() != null)
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
    public List<Assunto> findByDisciplinaId(UUID disciplinaId, String filter) throws Exception {
        if (filter == null || filter.isBlank()) {
            return assuntoRepository.findByDisciplinaId(disciplinaId);
        }

        Assunto assunto = FoxUtils.criarObjetoDinamico(filter, Assunto.class);
        assunto.setDisciplinaId(disciplinaId);

        ExampleMatcher matcher = FoxUtils.createExampleMatcher(Assunto.class);

        Iterable<Assunto> assuntos = assuntoRepository.findAll(Example.of(assunto, matcher));

        List<Assunto> response = StreamSupport.stream(assuntos.spliterator(), false)
                .collect(Collectors.toList());

        return response;
    }


    public void deletar(UUID assuntoId) {
        assuntoRepository.deleteById(assuntoId);
    }

    public Assunto findByIdAndDisciplinaId(UUID id, UUID assuntoId) throws Exception {
        return assuntoRepository.findByIdAndDisciplinaId(id, assuntoId);
    }


}