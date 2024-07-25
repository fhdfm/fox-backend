package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.*;
import br.com.foxconcursos.dto.*;
import br.com.foxconcursos.repositories.AlternativaRepository;
import br.com.foxconcursos.repositories.QuestaoRepository;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final JdbcTemplate jdbcTemplate;

    public QuestaoService(QuestaoRepository questaoRepository,
                          AlternativaRepository alternativaRepository,
                          JdbcTemplate jdbcTemplate) {

        this.questaoRepository = questaoRepository;
        this.alternativaRepository = alternativaRepository;
        this.jdbcTemplate = jdbcTemplate;

    }

    public ResultadoResponse isAlternativaCorreta(UUID questaoId, UUID alternativaId) {

        String queryIdCorreta = """
               select a.id
               from questoes q
                        inner join alternativas a on q.id = a.questao_id
               where q.id = ?
                 and  a.correta = true
                    """;

        ResultadoResponse resultadoResponse = new ResultadoResponse();

        jdbcTemplate.query(queryIdCorreta, rs -> {
            resultadoResponse.setAlternativaCorreta(UUID.fromString(rs.getString("id")));
            resultadoResponse.setCorreta(UUID.fromString(rs.getString("id")).equals(alternativaId));
        }, questaoId);

        return resultadoResponse;

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

        if (request.getAno() == null) {
            throw new IllegalArgumentException("Ano é obrigatório.");
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

    public Map<String, String> getFiltroCorrente(FiltroQuestao questao) {

        Map<String, String> filtros = new HashMap<String, String>();

        if (questao.getAssuntoId() != null
                && !questao.getAssuntoId().isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (UUID assunto : questao.getAssuntoId()) {
                sb.append(obterDescricaoPorPK(
                        "" + assunto, "assunto", "nome")).append(", ");
            }

            filtros.put("Assunto(s): ",
                    sb.toString().substring(
                            0, sb.toString().length() - 1));
        }

        if (questao.getDisciplinaId() != null && !questao.getDisciplinaId().isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < questao.getDisciplinaId().size(); i++) {
                sb.append(obterDescricaoPorPK(
                        "" + questao.getDisciplinaId().get(i), "disciplinas", "nome")).append(", ");
            }

            filtros.put("Disciplina(s): ",
                    sb.toString().substring(0, sb.toString().length() - 1));
        }

        if (questao.getInstituicaoId() != null)
            filtros.put("Instituição: ", obterDescricaoPorPK(
                    "" + questao.getInstituicaoId(), "instituicao", "nome"));

        if (questao.getCargoId() != null)
            filtros.put("Cargo: ", obterDescricaoPorPK(
                    "" + questao.getCargoId(), "cargo", "nome"));

        if (questao.getAno() != null)
            filtros.put("Ano: ", questao.getAno().toString());

        if (questao.getBancaId() != null)
            filtros.put("Banca: ", obterDescricaoPorPK(
                    "" + questao.getBancaId(), "bancas", "nome"));

        if (questao.getCidade() != null)
            filtros.put("Cidade:", questao.getCidade());

        if (questao.getUf() != null)
            filtros.put("UF: ", questao.getUf());

        if (questao.getEscolaridade() != null)
            filtros.put("Escolaridade: ", questao.getEscolaridade().toString());

        return filtros;
    }

    private String obterDescricaoPorPK(String id, String tabela, String campo) {
        return this.jdbcTemplate.queryForObject(
                "select " + campo + " from " + tabela + " where id = ?",
                String.class, UUID.fromString(id));
    }

    public List<QuestaoResponse> findAll(FiltroQuestao questao,
                                         Integer limit, Integer offset) {

        UsuarioLogado user = SecurityUtil.obterUsuarioLogado();
        boolean isAluno = user.isAluno();

        String sql = """
                   select q.id as qid,
                          q.enunciado,
                          q.ano,
                          q.uf,
                          q.escolaridade,
                          q.cidade,
                          a.id as aid,
                          a.descricao,
                          a.correta,
                          a.letra,
                          c.nome as cargo,
                          d.nome as disciplina,
                          i.nome as instituicao,
                          a2.nome as assunto,
                          b.nome as banca,
                """;

        if (isAluno)
            sql += " r.acerto as acerto," +
                    " r.alternativa_id as alternativaSelecionadaId, ";


        sql += """
                       count(cm.id) as comentario_count 
                       from questoes q
                                inner join alternativas a
                                           on a.questao_id = q.id
                                left join bancas b
                                          on q.banca_id = b.id
                                left join instituicao i
                                          on q.instituicao_id = i.id
                                left join cargo c
                                          on q.cargo_id = c.id
                                left join assunto a2
                                          on q.assunto_id = a2.id
                                left join disciplinas d
                                          on q.disciplina_id = d.id 
                                left join comentarios cm
                                          on cm.questao_id = q.id
                """;

        if (isAluno)
            sql += " left join respostas r on r.questao_id = q.id and r.usuario_id = '" + user.getId() + "'";

        sql += " where q.status = 'ATIVO'";

        if (questao.getAssuntoId() != null && !questao.getAssuntoId().isEmpty()) {
            sql += " and q.assunto_id in ("
                    + listToString(questao.getAssuntoId()) + ") ";
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

        if (questao.getDisciplinaId() != null && !questao.getDisciplinaId().isEmpty()) {
            sql += " and q.disciplina_id in ("
                    + listToString(questao.getDisciplinaId()) + ") ";
        }

        if (questao.getInstituicaoId() != null) {
            sql += " and q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
        }

        sql += """
                    GROUP BY 
                        q.id, q.enunciado, q.ano, q.uf, q.escolaridade, q.cidade, 
                        a.id, a.descricao, a.correta, a.letra, 
                        c.nome, d.nome, i.nome, a2.nome, b.nome                
                """;

        if (isAluno)
            sql += ", r.acerto, r.alternativa_id ";

        sql += " ORDER BY q.id limit " + limit + " offset " + offset;

        Map<UUID, QuestaoResponse> questaoMap = new HashMap<UUID, QuestaoResponse>();
        List<QuestaoResponse> result = new ArrayList<QuestaoResponse>();

        this.jdbcTemplate.query(sql, rs -> {

            UUID questaoId = UUID.fromString(rs.getString("qid"));
            QuestaoResponse qr = questaoMap.get(questaoId);

            if (qr == null) {
                qr = new QuestaoResponse();
                qr.setId(questaoId);
                qr.setEnunciado(rs.getString("enunciado"));
                qr.setBanca(rs.getString("banca"));
                qr.setAno(rs.getInt("ano"));
                qr.setInstituicao(rs.getString("instituicao"));
                qr.setDisciplina(rs.getString("disciplina"));
                qr.setCargo(rs.getString("cargo"));
                qr.setAssunto(rs.getString("assunto"));
                if (isAluno) {
                    String acerto = rs.getObject("acerto")
                            != null ? (rs.getBoolean("acerto") ? "true" : "false") : null;
                    qr.setAcerto(acerto);
                }
                qr.setComentarios(rs.getInt("comentario_count"));
                qr.setAlternativaSelecionadaId(
                        rs.getString("alternativaSelecionadaId") != null ?
                                UUID.fromString(rs.getString("alternativaSelecionadaId")) :
                                null);
                qr.setAlternativas(new ArrayList<>());

                questaoMap.put(questaoId, qr);
            }

            AlternativaResponse alternativa = new AlternativaResponse();
            alternativa.setId(UUID.fromString(rs.getString("aid")));
            alternativa.setLetra(rs.getString("letra"));

            alternativa.setCorreta(isAluno && qr.getAcerto() != null || !isAluno ? rs.getBoolean("correta") : false);
            alternativa.setDescricao(rs.getString("descricao"));

            qr.getAlternativas().add(alternativa);

        });

        result.addAll(questaoMap.values());
        return result;
    }

    public int getRecordCount(FiltroQuestao questao) {
        String sql = """
                    select count(q.id) from questoes q where q.status = 'ATIVO' 
                """;

        if (questao.getAssuntoId() != null && !questao.getAssuntoId().isEmpty()) {
            sql += " and q.assunto_id in ("
                    + listToString(questao.getAssuntoId()) + ") ";
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

        if (questao.getDisciplinaId() != null && !questao.getDisciplinaId().isEmpty()) {
            sql += " and q.disciplina_id in ("
                    + listToString(questao.getDisciplinaId()) + ") ";
        }

        if (questao.getInstituicaoId() != null) {
            sql += " and q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
        }

        int count = this.jdbcTemplate.queryForObject(sql, Integer.class);

        return count;
    }


    public QuestaoResponse findById(UUID id) {

        UsuarioLogado user = SecurityUtil.obterUsuarioLogado();
        boolean isAluno = user.isAluno();

        String sql = """
                        select q.id as qid,
                               q.enunciado,
                               q.escolaridade,
                               q.ano,
                               q.uf,
                               q.cidade,
                               a.id as aid,
                               a.letra,
                               a.correta,
                               a.descricao,
                               b.id as bid,
                               i.id as iid,
                               c.id as cid,
                               d.id as did,
                               a2.id as a2id,
                """;

        if (isAluno)
            sql += " r.acerto as acerto, ";

        sql += """
                    COUNT(cm.id) AS comentario_count
                    from questoes q
                                inner join alternativas a
                                        on a.questao_id = q.id
                                left join bancas b on q.banca_id = b.id
                                left join instituicao i on q.instituicao_id = i.id
                                left join cargo c on q.cargo_id = c.id
                                left join assunto a2 on q.assunto_id = a2.id
                                left join disciplinas d on q.disciplina_id = d.id
                                LEFT JOIN comentarios cm ON cm.questao_id = q.id
                """;

        if (isAluno)
            sql += " left join respostas r on r.questao_id and r.usuario_id = '" + user.getId() + "' ";

        sql += """
                    where q.status = 'ATIVO' and q.id = ? 
                    group by q.id, q.enunciado, q.ano, q.uf, q.escolaridade, q.cidade, 
                    a.id, a.descricao, a.correta, a.letra, 
                    c.nome, d.nome, i.nome, a2.nome, b.nome 
                """;

        if (isAluno)
            sql += ", r.acerto ";

        sql += " ORDER BY q.id";

        QuestaoResponse questaoResponse = jdbcTemplate.query(sql, rs -> {
            QuestaoResponse qr = null;

            while (rs.next()) {
                if (qr == null) {
                    qr = new QuestaoResponse();
                    qr.setId(UUID.fromString(rs.getString("qid")));
                    qr.setEnunciado(rs.getString("enunciado"));
                    qr.setEscolaridade(rs.getString("escolaridade"));
                    qr.setUf(rs.getString("uf"));
                    qr.setCidade(rs.getString("cidade"));
                    qr.setAno(rs.getInt("ano"));
                    qr.setBancaId(UUID.fromString(rs.getString("bid")));
                    qr.setDisciplinaId(UUID.fromString(rs.getString("did")));
                    qr.setInstituicaoId(UUID.fromString(rs.getString("iid")));
                    qr.setCargoId(UUID.fromString(rs.getString("cid")));
                    qr.setAssuntoId(UUID.fromString(rs.getString("a2id")));
                    if (isAluno) {
                        String acerto = rs.getObject("acerto")
                                != null ? (rs.getBoolean("acerto") ? "true" : "false") : null;
                        qr.setAcerto(acerto);
                    }
                    qr.setComentarios(rs.getInt("comentario_count"));
                    qr.setAlternativas(new ArrayList<>());
                }

                AlternativaResponse alternativa = new AlternativaResponse();
                alternativa.setId(UUID.fromString(rs.getString("aid")));
                alternativa.setLetra(rs.getString("letra"));
                alternativa.setDescricao(rs.getString("descricao"));
                alternativa.setCorreta(!isAluno && rs.getBoolean("correta"));

                qr.getAlternativas().add(alternativa);
            }

            return qr;
        }, id);

        return questaoResponse;
    }


    private String listToString(List<UUID> list) {
        StringBuilder sb = new StringBuilder();
        if (list.size() == 1) {
            return "'" + list.get(0) + "'";
        }

        for (int i = 0; i < list.size(); i++) {
            sb.append("'").append(list.get(i)).append("',");
        }

        return sb.substring(0, sb.length() - 1);
    }
}
