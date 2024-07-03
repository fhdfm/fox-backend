package br.com.foxconcursos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Alternativa;
import br.com.foxconcursos.domain.Questao;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.dto.AlternativaRequest;
import br.com.foxconcursos.dto.AlternativaResponse;
import br.com.foxconcursos.dto.QuestaoRequest;
import br.com.foxconcursos.dto.QuestaoResponse;
import br.com.foxconcursos.repositories.AlternativaRepository;
import br.com.foxconcursos.repositories.QuestaoRepository;

@Service
public class QuestaoService {
    
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final JdbcTemplate jdbcTemplate;

    public QuestaoService(QuestaoRepository questaoRepository, 
        AlternativaRepository alternativaRepository, JdbcTemplate jdbcTemplate) {
        
        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.jdbcTemplate = jdbcTemplate;

    }

    @Transactional
    public UUID create(QuestaoRequest request) {
        
        validate(request);

        Questao questao = new Questao(request);
        questao = this.questaoRepository.save(questao);

        UUID questaoId = questao.getId();

        List<AlternativaRequest> alternativas = request.getAlternativas();
        for (AlternativaRequest ar : alternativas) {
            Alternativa alternativa = new Alternativa(ar, questaoId);
            this.alternativaRepository.save(alternativa);
        }
        
        return questaoId;
    }

    @Transactional
    public UUID update(QuestaoRequest request, UUID id) {
        
        validate(request);

        Questao questao = this.questaoRepository.findByIdAndStatus(id, Status.ATIVO)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Questão não encontrada com o id: " + id));
        
        questao.setEnunciado(request.getEnunciado());
        questao.setDisciplinaId(request.getDisciplinaId());
        questao.setAssuntoId(request.getAssuntoId());
        questao.setBancaId(request.getBancaId());
        questao.setInstituicaoId(request.getInstituicaoId());
        questao.setCargoId(request.getCargoId());
        questao.setAno(request.getAno());
        questao.setUf(request.getUf());
        questao.setCidade(request.getCidade());
        questao.setEscolaridade(request.getEscolaridade());

        this.questaoRepository.save(questao);

        List<AlternativaRequest> alternativas = request.getAlternativas();
        for (AlternativaRequest ar : alternativas) {
            
            Alternativa alternativa =
                this.alternativaRepository.findById(ar.getId()).orElse(null);
            
            if (alternativa != null) {
                alternativa.setLetra(ar.getOrdem());
                alternativa.setDescricao(ar.getDescricao());
                alternativa.setCorreta(ar.getCorreta());
                this.alternativaRepository.save(alternativa);
            }
           
        }
        
        return id;
    }


    private void validate(QuestaoRequest request) {

        if (request.getEnunciado() == null || request.getEnunciado().trim().isEmpty()) {
            throw new IllegalArgumentException("Enunciado é obrigatória.");
        }

        if (request.getDisciplinaId() == null) {
            throw new IllegalArgumentException("Disciplina é obrigatória.");
        }

        if (request.getAssuntoId() == null) {
            throw new IllegalArgumentException("Assunto é obrigatório.");
        }

        if (request.getBancaId() == null) {
            throw new IllegalArgumentException("Banca é obrigatória.");
        }

        if (request.getInstituicaoId() == null) {
            throw new IllegalArgumentException("Instituição é obrigatória.");
        }

        if (request.getCargoId() == null) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }

        if (request.getAno() == null) {
            throw new IllegalArgumentException("Ano é obrigatório.");
        }

        if (request.getUf() == null) {
            throw new IllegalArgumentException("UF é obrigatória.");
        }

        if (request.getCidade() == null) {
            throw new IllegalArgumentException("Cidade é obrigatória.");
        }

        if (request.getEscolaridade() == null) {
            throw new IllegalArgumentException("Escolaridade é obrigatória.");
        }

        if (request.getAlternativas() == null || request.getAlternativas().isEmpty()) {
            throw new IllegalArgumentException("Alternativas são obrigatórias.");
        }

        List<AlternativaRequest> alternativas = request.getAlternativas();
        for (AlternativaRequest ar : alternativas) {
            if (ar.getOrdem() == null || ar.getOrdem().trim().isEmpty()) {
                throw new IllegalArgumentException("Ordem da alternativa é obrigatório.");
            }

            if (ar.getDescricao() == null || ar.getDescricao().trim().isEmpty()) {
                throw new IllegalArgumentException("Descrição da alternativa é obrigatório.");
            }

            if (ar.getCorreta() == null) {
                throw new IllegalArgumentException("Indicador de correta é obrigatório.");
            }
        }

    }

    public void delete(UUID id) {
        Questao questao = this.questaoRepository.findByIdAndStatus(id, Status.ATIVO)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Questão não encontrada com o id: " + id));
        questao.setStatus(Status.INATIVO);
        this.questaoRepository.save(questao);
    }

    public Map<String, String> getFiltroCorrente(Questao questao) {
        
        Map<String, String> filtros = new HashMap<String, String>();
        
        if (questao.getAssuntoId() != null)
            filtros.put("Assunto:", obterDescricaoPorPK(
                questao.getAssuntoId(), "assunto", "nome"));

        if (questao.getDisciplinaId() != null)
            filtros.put("Disciplina:", obterDescricaoPorPK(
                questao.getDisciplinaId(), "disciplinas", "nome"));

        if (questao.getInstituicaoId() != null)
            filtros.put("Instituição:", obterDescricaoPorPK(
                questao.getInstituicaoId(), "instituicao", "nome"));

        if (questao.getCargoId() != null)
            filtros.put("Cargo:", obterDescricaoPorPK(
                questao.getCargoId(), "cargo", "nome"));

        if (questao.getAno() != null)
            filtros.put("Ano:", questao.getAno().toString());
        
        if (questao.getBancaId() != null)
            filtros.put("Banca:", obterDescricaoPorPK(
                questao.getBancaId(), "bancas", "nome"));
        
        if (questao.getCidade() != null)
            filtros.put("Cidade:", questao.getCidade());

        if (questao.getUf() != null)
            filtros.put("UF:", questao.getUf());

        if (questao.getEscolaridade() != null)
            filtros.put("Escolaridade:", questao.getEscolaridade().toString());

        return filtros;
    }

    private String obterDescricaoPorPK(UUID id, String tabela, String campo) {
        return this.jdbcTemplate.queryForObject(
                "select " + campo + " from " + tabela + " where id = ?",
                String.class, id);
    }

    public List<QuestaoResponse> findAll(Questao questao,
        Integer limit, Integer offset) {
        
        String sql = """
            select q.id as qid, q.enunciado, a.id as aid, a.letra, a.descricao 
            from questoes q where q.status = 'ATIVO' inner join alternativas a 
            on a.questao_id = q.id 
        """;
        
        if (questao.getAssuntoId() != null) {
            sql += " and q.assunto_id = '" + questao.getAssuntoId() + "' ";
        }

        if (questao.getAno() != null) {
            sql += " and q.ano = " + questao.getAno();
        }

        if (questao.getBancaId() != null) {
            sql += " and q.banca_id = '" + questao.getBancaId() + "' ";
        }

        if (questao.getCidade() != null) {
            sql += " and q.cidade = '" + questao.getCidade() + "' ";
        }

        if (questao.getUf() != null) {
            sql += " and q.uf = '" + questao.getUf() + "' ";
        }
        
        if (questao.getEscolaridade() != null) {
            sql += " and q.escolaridade = '" + questao.getEscolaridade() + "' ";
        }

        if (questao.getEnunciado() != null && !questao.getEnunciado().trim().isEmpty()) {
            sql += " and q.enunciado like '%" + questao.getEnunciado() + "%' ";
        }

        if (questao.getCargoId() != null) {
            sql += " and q.cargo_id = '" + questao.getCargoId() + "' ";
        }

        if (questao.getDisciplinaId() != null) {
            sql += " and q.disciplina_id = '" + questao.getDisciplinaId() + "' ";
        }

        if (questao.getInstituicaoId() != null) {
            sql += " and q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
        }

        sql += "limit " + limit + " offset " + offset;

        Map<UUID, QuestaoResponse> questaoMap = new HashMap<UUID, QuestaoResponse>();
        List<QuestaoResponse> result = new ArrayList<QuestaoResponse>();

        this.jdbcTemplate.query(sql, rs -> {

            UUID questaoId = UUID.fromString(rs.getString("qid"));
            QuestaoResponse qr = questaoMap.get(questaoId);
    
            if (qr == null) {
                qr = new QuestaoResponse();
                qr.setId(questaoId);
                qr.setEnunciado(rs.getString("enunciado"));
                qr.setAlternativas(new ArrayList<>());
                questaoMap.put(questaoId, qr);
            }
    
            AlternativaResponse alternativa = new AlternativaResponse();
            alternativa.setId(UUID.fromString(rs.getString("aid")));
            alternativa.setLetra(rs.getString("letra"));
            alternativa.setDescricao(rs.getString("descricao"));
    
            qr.getAlternativas().add(alternativa);

        });

        result.addAll(questaoMap.values());
        return result;
    }

    public int getRecordCount(Questao questao) {

        String sql = """
            select q.id(*) from questoes q where q.status = 'ATIVO' 
        """;
        
        if (questao.getAssuntoId() != null) {
            sql += " and q.assunto_id = '" + questao.getAssuntoId() + "' ";
        }

        if (questao.getAno() != null) {
            sql += " and q.ano = " + questao.getAno();
        }

        if (questao.getBancaId() != null) {
            sql += " and q.banca_id = '" + questao.getBancaId() + "' ";
        }

        if (questao.getCidade() != null) {
            sql += " and q.cidade = '" + questao.getCidade() + "' ";
        }

        if (questao.getUf() != null) {
            sql += " and q.uf = '" + questao.getUf() + "' ";
        }
        
        if (questao.getEscolaridade() != null) {
            sql += " and q.escolaridade = '" + questao.getEscolaridade() + "' ";
        }

        if (questao.getEnunciado() != null && !questao.getEnunciado().trim().isEmpty()) {
            sql += " and q.enunciado like '%" + questao.getEnunciado() + "%' ";
        }

        if (questao.getCargoId() != null) {
            sql += " and q.cargo_id = '" + questao.getCargoId() + "' ";
        }

        if (questao.getDisciplinaId() != null) {
            sql += " and q.disciplina_id = '" + questao.getDisciplinaId() + "' ";
        }

        if (questao.getInstituicaoId() != null) {
            sql += " and q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
        }

        int count = this.jdbcTemplate.queryForObject(sql, Integer.class);

        return count;
    }

    public QuestaoResponse findById(UUID id) {
        
        QuestaoResponse qr = new QuestaoResponse();

        String sql = """
            select q.id as qid, q.enunciado, a.id as aid, a.letra, a.descricao 
            from questoes q where q.status = 'ATIVO' inner join alternativas a 
            on a.questao_id = q.id and q.id = ?
        """;
        
        jdbcTemplate.query(sql, rs -> {

            if (qr.getId() == null) {
                qr.setId(UUID.fromString(rs.getString("qid")));
                qr.setEnunciado(rs.getString("enunciado"));
                qr.setAlternativas(new ArrayList<>());
            }

            AlternativaResponse alternativa = new AlternativaResponse();
            alternativa.setId(UUID.fromString(rs.getString("aid")));
            alternativa.setLetra(rs.getString("letra"));
            alternativa.setDescricao(rs.getString("descricao"));

            qr.getAlternativas().add(alternativa);

            return qr;

        }, id);

        return qr;
    }

}
