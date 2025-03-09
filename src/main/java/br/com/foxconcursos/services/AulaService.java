package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Aula;
import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.*;
import br.com.foxconcursos.repositories.AulaConteudoRepository;
import br.com.foxconcursos.repositories.AulaRepository;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AulaService {

    private static final String NULL_VALUE = "00000000-0000-0000-0000-000000000000";

    private final AulaRepository repository;
    private final AulaConteudoRepository conteudoRepository;
    private final StorageService storageService;
    private final JdbcTemplate jdbcTemplate;

    public AulaService(AulaRepository repository,
                       AulaConteudoRepository conteudoRepository,
                       StorageService storageService,
                       JdbcTemplate jdbcTemplate) {

        this.repository = repository;
        this.conteudoRepository = conteudoRepository;
        this.storageService = storageService;
        this.jdbcTemplate = jdbcTemplate;

    }

    public UUID criarAula(AulaRequest request) {
        request.validate();
        Aula aula = request.toModel();
        this.repository.save(aula);
        return aula.getId();
    }

    @Transactional
    public void deletarAula(UUID aulaId) {

        List<AulaConteudo> anexos = this.conteudoRepository.findByAulaId(aulaId);
        this.excluirProgresso(aulaId);

        for (AulaConteudo anexo : anexos) {
            this.storageService.delete(anexo.getKey());
        }

        this.conteudoRepository.deleteByAulaId(aulaId);
        this.repository.deleteById(aulaId);
    }

    public void excluirProgresso(UUID aulaId) {
        String sql = "DELETE FROM progresso WHERE aula_id = ?";

        jdbcTemplate.update(sql, aulaId);
    }

    public void atualizarAula(UUID id, AulaRequest request) {
        request.validate();

        Aula aula = this.repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Aula: '" + id + "' não encontrada."));

        aula.updateFromRequest(request);

        this.repository.save(aula);
    }

    @Transactional
    public UUID criarConteudo(UUID aulaId, AulaConteudoRequest request)
            throws Exception {

        request.validate(false);

        AulaConteudo conteudo = request.toModel();
        conteudo.setAulaId(aulaId);
        if (request.getVideoId() == null) {
            StorageOutput output = uploadArquivo(request.getFile());

            conteudo.setKey(output.getKey());
            conteudo.setUrl(output.getUrl());
            conteudo.setMimetype(output.getMimeType());
        } else {
            AulaConteudo response = this.conteudoRepository.findById(UUID.fromString(request.getVideoId()))
                    .orElseThrow(() -> new IllegalArgumentException("Conteúdo não encontrado para URL ID: " + request.getVideoId()));

            conteudo.setKey(response.getKey());
            conteudo.setUrl(response.getUrl());
            conteudo.setMimetype(response.getMimetype());
        }

        this.conteudoRepository.save(conteudo);
        return conteudo.getId();
    }

    private StorageOutput uploadArquivo(final MultipartFile file) throws Exception {
        final StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(file.getOriginalFilename())
                .withMimeType(file.getContentType())
                .withFileSize(file.getSize())
                .isPublic(false)
                .build();

        return this.storageService.upload(input);
    }

    @Transactional
    public void atualizarConteudo(UUID aulaId, UUID conteudoId, AulaConteudoRequest request)
            throws Exception {

        request.validate(true);

        AulaConteudo conteudo = this.conteudoRepository.findById(conteudoId).orElseThrow(
                () -> new IllegalStateException("Conteudo: '" + conteudoId + "' não encontrado."));

        conteudo.setTitulo(request.getTitulo());
        conteudo.setTipo(request.getTipo());

        if (request.hasMedia() && request.getVideoId() == null) {
            StorageOutput output = uploadArquivo(request.getFile());

            conteudo.setKey(output.getKey());
            conteudo.setUrl(output.getUrl());
            conteudo.setMimetype(output.getMimeType());
        } else {
            AulaConteudo response = this.conteudoRepository.findById(UUID.fromString(request.getVideoId()))
                    .orElseThrow(() -> new IllegalArgumentException("Conteúdo não encontrado para URL ID: " + request.getVideoId()));

            conteudo.setKey(response.getKey());
            conteudo.setUrl(response.getUrl());
            conteudo.setMimetype(response.getMimetype());
        }
        this.conteudoRepository.save(conteudo);
    }

    public List<AulaResponse> buscarPorParametros(String titulo, UUID cursoId, UUID disciplinaId) {
        UsuarioLogado usuario = SecurityUtil.obterUsuarioLogado();

        String sql = getSqlBase();
        sql += " where 1=1 ";

        if (!titulo.isEmpty())
            sql += " and a.titulo like '%" + titulo + "%' ";

        if (!cursoId.equals(UUID.fromString(NULL_VALUE)))
            sql += " and c.id = '" + cursoId + "' ";

        if (!disciplinaId.equals(UUID.fromString(NULL_VALUE)))
            sql += " and d.id = '" + disciplinaId + "' ";

        List<AulaResponse> response = new ArrayList<>();

        jdbcTemplate.query(sql, (rs) -> {
            AulaResponse aula = new AulaResponse();
            aula.setId(rs.getObject("id", UUID.class));
            aula.setTitulo(rs.getString("titulo"));
            aula.setCurso(rs.getString("curso"));
            aula.setDisciplina(rs.getString("disciplina"));
            response.add(aula);
        });

        return response;
    }

    public AulaResponse buscarPorId(UUID id) throws IOException {
        String sql = getSqlBase();
        sql += " where a.id = ?";

        AulaResponse response = jdbcTemplate.query(sql, (rs) -> {
            AulaResponse aula = new AulaResponse();
            if (rs.next()) {
                aula.setId(rs.getObject("id", UUID.class));
                aula.setTitulo(rs.getString("titulo"));
                aula.setCurso(rs.getString("curso"));
                aula.setDisciplina(rs.getString("disciplina"));
                aula.setDisciplinaId(rs.getObject("disciplina_id", UUID.class));
                return aula;
            }
            return aula;
        }, id);

        List<AulaConteudo> anexos = this.conteudoRepository.findByAulaId(id);
        List<ConteudoResponse> conteudoResponse = new ArrayList<>();
        for (AulaConteudo anexo : anexos) {
            ConteudoResponse content = anexo.toAssembly();
//            if (anexo.getVimeo() == null) {
            String url = this.storageService.getLink(anexo.getKey());
            content.setUrl(url);
//            }else{
//                content.setVimeo(anexo.getVimeo());
//            }

            conteudoResponse.add(content);
        }

        response.setConteudo(conteudoResponse);

        return response;
    }

    @Transactional
    public void deletarConteudo(UUID id) {
        AulaConteudo conteudo = this.conteudoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Conteudo: '" + id + "' não encontrado."));
        this.storageService.delete(conteudo.getKey());
        this.conteudoRepository.delete(conteudo);
    }

    private String getSqlBase() {
        String sql = "select a.id as id, a.titulo as titulo, c.titulo as curso, d.nome ";
        sql += "as disciplina, d.id as disciplina_id from ";
        sql += "aulas a inner join cursos c on a.curso_id = c.id ";
        sql += "inner join disciplinas d on a.disciplina_id = d.id ";
        return sql;
    }

    public UUID findCursoIdByFileId(String fileId) {
        return this.conteudoRepository.findCursoIdByFileId(fileId);
    }

    public List<AulaConteudo> buscarVideoAulasCadastradas() {
        String sql = "SELECT key, " +
                "               (ARRAY_AGG(id))[1] AS id, " +
                "               (ARRAY_AGG(url))[1] AS url, " +
                "               (ARRAY_AGG(titulo))[1] AS titulo " +
                "        FROM aula_conteudo " +
                "        WHERE tipo = 'VIDEO' " +
                "        GROUP BY key";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AulaConteudo aula = new AulaConteudo();
            String aulaKey = rs.getString("key");
            aula.setId(rs.getObject("id", UUID.class));
            aula.setTitulo(rs.getString("titulo"));

            try {
                aula.setUrl(storageService.getLink(aulaKey));
            } catch (IOException e) {
                throw new RuntimeException("Erro ao obter o link do vídeo", e);
            }

            return aula;
        });
    }

}
