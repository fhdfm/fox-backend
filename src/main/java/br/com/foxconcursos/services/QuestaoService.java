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
import br.com.foxconcursos.domain.FiltroQuestao;
import br.com.foxconcursos.domain.Questao;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.AlternativaRequest;
import br.com.foxconcursos.dto.AlternativaResponse;
import br.com.foxconcursos.dto.QuestaoRequest;
import br.com.foxconcursos.dto.QuestaoResponse;
import br.com.foxconcursos.repositories.AlternativaRepository;
import br.com.foxconcursos.repositories.QuestaoRepository;
import br.com.foxconcursos.util.SecurityUtil;
    
    public class QuestaoService {
    
        private JdbcTemplate jdbcTemplate;
    
        public QuestaoService(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
    
        public List<QuestaoResponse> findAll(QuestaoFiltro questao, int limit, int offset) {
            UsuarioLogado user = SecurityUtil.obterUsuarioLogado();
            boolean isAluno = user.isAluno();
    
            String sql = """
                WITH PagedQuestions AS (
                    SELECT 
                        q.id as qid,
                        q.enunciado,
                        q.ano,
                        q.uf,
                        q.escolaridade,
                        q.cidade,
                        c.nome as cargo,
                        d.nome as disciplina,
                        i.nome as instituicao,
                        a2.nome as assunto,
                        b.nome as banca,
            """;
    
            if (isAluno)
                sql += " r.acerto as acerto, ";
    
            sql += """
                        COUNT(cm.id) as comentario_count,
                        ROW_NUMBER() OVER (ORDER BY q.id) as row_num
                    FROM questoes q
                        LEFT JOIN bancas b ON q.banca_id = b.id
                        LEFT JOIN instituicao i ON q.instituicao_id = i.id
                        LEFT JOIN cargo c ON q.cargo_id = c.id
                        LEFT JOIN assunto a2 ON q.assunto_id = a2.id
                        LEFT JOIN disciplinas d ON q.disciplina_id = d.id 
                        LEFT JOIN comentarios cm ON cm.questao_id = q.id
            """;
    
            if (isAluno)
                sql += " LEFT JOIN respostas r ON r.questao_id = q.id AND r.usuario_id = '" + user.getId() + "'";
    
            sql += " WHERE q.status = 'ATIVO'";
    
            if (questao.getAssuntoId() != null && !questao.getAssuntoId().isEmpty()) {
                sql += " AND q.assunto_id IN (" + listToString(questao.getAssuntoId()) + ") ";
            }
    
            if (questao.getAno() != null) {
                sql += " AND q.ano = " + questao.getAno();
            }
    
            if (questao.getBancaId() != null) {
                sql += " AND q.banca_id = '" + questao.getBancaId() + "' ";
            }
    
            if (questao.getCidade() != null) {
                sql += " AND q.cidade = '" + questao.getCidade() + "' ";
            }
    
            if (questao.getUf() != null) {
                sql += " AND q.uf = '" + questao.getUf() + "' ";
            }
    
            if (questao.getEscolaridade() != null) {
                sql += " AND q.escolaridade = '" + questao.getEscolaridade() + "' ";
            }
    
            if (questao.getEnunciado() != null && !questao.getEnunciado().trim().isEmpty()) {
                sql += " AND q.enunciado LIKE '%" + questao.getEnunciado() + "%' ";
            }
    
            if (questao.getCargoId() != null) {
                sql += " AND q.cargo_id = '" + questao.getCargoId() + "' ";
            }
    
            if (questao.getDisciplinaId() != null && !questao.getDisciplinaId().isEmpty()) {
                sql += " AND q.disciplina_id IN (" + listToString(questao.getDisciplinaId()) + ") ";
            }
    
            if (questao.getInstituicaoId() != null) {
                sql += " AND q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
            }
    
            sql += """
                    GROUP BY 
                        q.id, q.enunciado, q.ano, q.uf, q.escolaridade, q.cidade, 
                        c.nome, d.nome, i.nome, a2.nome, b.nome
            """;
    
            if (isAluno)
                sql += ", r.acerto";
    
            sql += """
                )
                SELECT 
                    pq.qid, pq.enunciado, pq.ano, pq.uf, pq.escolaridade, pq.cidade,
                    pq.cargo, pq.disciplina, pq.instituicao, pq.assunto, pq.banca,
                    pq.acerto, pq.comentario_count, a.id as aid, a.descricao, a.correta, a.letra
                FROM PagedQuestions pq
                LEFT JOIN alternativas a ON a.questao_id = pq.qid
                WHERE pq.row_num BETWEEN ? AND ?
                ORDER BY pq.qid, a.id
            """;
    
            // Calcule os limites da página
            int startRow = offset + 1;
            int endRow = offset + limit;
    
            Map<UUID, QuestaoResponse> questaoMap = new HashMap<UUID, QuestaoResponse>();
            List<QuestaoResponse> result = new ArrayList<QuestaoResponse>();
    
            this.jdbcTemplate.query(sql, new Object[]{startRow, endRow}, rs -> {
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
                        String acerto = rs.getObject("acerto") != null ? (rs.getBoolean("acerto") ? "true" : "false") : null;
                        qr.setAcerto(acerto);
                    }
                    qr.setComentarios(rs.getInt("comentario_count"));
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
    
        private String listToString(List<String> list) {
            return String.join(",", list);
        }
    
    // public List<QuestaoResponse> findAll(FiltroQuestao questao,
    //                                      Integer limit, Integer offset) {

    //     UsuarioLogado user = SecurityUtil.obterUsuarioLogado();
    //     boolean isAluno = user.isAluno();

    //     String sql = """
    //                select q.id as qid,
    //                       q.enunciado,
    //                       q.ano,
    //                       q.uf,
    //                       q.escolaridade,
    //                       q.cidade,
    //                       a.id as aid,
    //                       a.descricao,
    //                       a.correta,
    //                       a.letra,
    //                       c.nome as cargo,
    //                       d.nome as disciplina,
    //                       i.nome as instituicao,
    //                       a2.nome as assunto,
    //                       b.nome as banca,
    //             """;

    //     if (isAluno)
    //         sql += " r.acerto as acerto, ";


    //     sql += """
    //                    count(cm.id) as comentario_count 
    //                    from questoes q
    //                             inner join alternativas a
    //                                        on a.questao_id = q.id
    //                             left join bancas b
    //                                       on q.banca_id = b.id
    //                             left join instituicao i
    //                                       on q.instituicao_id = i.id
    //                             left join cargo c
    //                                       on q.cargo_id = c.id
    //                             left join assunto a2
    //                                       on q.assunto_id = a2.id
    //                             left join disciplinas d
    //                                       on q.disciplina_id = d.id 
    //                             left join comentarios cm
    //                                       on cm.questao_id = q.id
    //             """;

    //     if (isAluno)
    //         sql += " left join respostas r on r.questao_id = q.id and r.usuario_id = '" + user.getId() + "'";

    //     sql += " where q.status = 'ATIVO'";

    //     if (questao.getAssuntoId() != null && !questao.getAssuntoId().isEmpty()) {
    //         sql += " and q.assunto_id in ("
    //                 + listToString(questao.getAssuntoId()) + ") ";
    //     }

    //     if (questao.getAno() != null) {
    //         sql += " and q.ano = " + questao.getAno();
    //     }

    //     if (questao.getBancaId() != null) {
    //         sql += " and q.banca_id = '" + questao.getBancaId() + "' ";
    //     }

    //     if (questao.getCidade() != null) {
    //         sql += " and q.cidade = '" + questao.getCidade() + "' ";
    //     }

    //     if (questao.getUf() != null) {
    //         sql += " and q.uf = '" + questao.getUf() + "' ";
    //     }

    //     if (questao.getEscolaridade() != null) {
    //         sql += " and q.escolaridade = '" + questao.getEscolaridade() + "' ";
    //     }

    //     if (questao.getEnunciado() != null && !questao.getEnunciado().trim().isEmpty()) {
    //         sql += " and q.enunciado like '%" + questao.getEnunciado() + "%' ";
    //     }

    //     if (questao.getCargoId() != null) {
    //         sql += " and q.cargo_id = '" + questao.getCargoId() + "' ";
    //     }

    //     if (questao.getDisciplinaId() != null && !questao.getDisciplinaId().isEmpty()) {
    //         sql += " and q.disciplina_id in ("
    //                 + listToString(questao.getDisciplinaId()) + ") ";
    //     }

    //     if (questao.getInstituicaoId() != null) {
    //         sql += " and q.instituicao_id = '" + questao.getInstituicaoId() + "' ";
    //     }

    //     sql += """
    //                 GROUP BY 
    //                     q.id, q.enunciado, q.ano, q.uf, q.escolaridade, q.cidade, 
    //                     a.id, a.descricao, a.correta, a.letra, 
    //                     c.nome, d.nome, i.nome, a2.nome, b.nome                
    //             """;

    //     if (isAluno)
    //         sql += ", r.acerto ";

    //     sql += " ORDER BY q.id limit " + limit + " offset " + offset;

    //     Map<UUID, QuestaoResponse> questaoMap = new HashMap<UUID, QuestaoResponse>();
    //     List<QuestaoResponse> result = new ArrayList<QuestaoResponse>();

    //     this.jdbcTemplate.query(sql, rs -> {

    //         UUID questaoId = UUID.fromString(rs.getString("qid"));
    //         QuestaoResponse qr = questaoMap.get(questaoId);

    //         if (qr == null) {
    //             qr = new QuestaoResponse();
    //             qr.setId(questaoId);
    //             qr.setEnunciado(rs.getString("enunciado"));
    //             qr.setBanca(rs.getString("banca"));
    //             qr.setAno(rs.getInt("ano"));
    //             qr.setInstituicao(rs.getString("instituicao"));
    //             qr.setDisciplina(rs.getString("disciplina"));
    //             qr.setCargo(rs.getString("cargo"));
    //             qr.setAssunto(rs.getString("assunto"));
    //             if (isAluno) {
    //                 String acerto = rs.getObject("acerto")
    //                         != null ? (rs.getBoolean("acerto") ? "true" : "false") : null;
    //                 qr.setAcerto(acerto);
    //             }
    //             qr.setComentarios(rs.getInt("comentario_count"));
    //             qr.setAlternativas(new ArrayList<>());
    //             questaoMap.put(questaoId, qr);
    //         }

    //         AlternativaResponse alternativa = new AlternativaResponse();
    //         alternativa.setId(UUID.fromString(rs.getString("aid")));
    //         alternativa.setLetra(rs.getString("letra"));

    //         alternativa.setCorreta(isAluno && qr.getAcerto() != null || !isAluno ? rs.getBoolean("correta") : false);
    //         alternativa.setDescricao(rs.getString("descricao"));

    //         qr.getAlternativas().add(alternativa);

    //     });

    //     result.addAll(questaoMap.values());
    //     return result;
    // }

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
