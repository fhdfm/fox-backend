package br.com.foxconcursos.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.domain.Aula;
import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.dto.AulaConteudoRequest;
import br.com.foxconcursos.dto.AulaRequest;
import br.com.foxconcursos.dto.AulaResponse;
import br.com.foxconcursos.dto.ConteudoResponse;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.AulaConteudoRepository;
import br.com.foxconcursos.repositories.AulaRepository;

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

        for (AulaConteudo anexo : anexos) {
            this.storageService.delete(anexo.getKey());
        }
        
        this.conteudoRepository.deleteByAulaId(aulaId);
        this.repository.deleteById(aulaId);
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

        MultipartFile file = request.getFile();


        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(file.getOriginalFilename())
                .withMimeType(file.getContentType())
                .withFileSize(file.getSize())
                .isPublic(false)
                .build();
        
        StorageOutput output = this.storageService.upload(input);
        
        conteudo.setKey(output.getKey());
        conteudo.setUrl(output.getUrl());
        conteudo.setMimetype(output.getMimeType());

        this.conteudoRepository.save(conteudo);
        return conteudo.getId();
    }

    @Transactional
    public void atualizarConteudo(UUID aulaId, UUID conteudoId, AulaConteudoRequest request) 
            throws Exception {
        
        request.validate(true);

        AulaConteudo conteudo = this.conteudoRepository.findById(conteudoId).orElseThrow(
                () -> new IllegalStateException("Conteudo: '" + conteudoId + "' não encontrado."));
        
        conteudo.setTitulo(request.getTitulo());
        conteudo.setTipo(request.getTipo());

        if (request.hasMedia()) {
            
            MultipartFile file = request.getFile();

            StorageInput input = new StorageInput.Builder()
                    .withFileInputStream(file.getInputStream())
                    .withFileName(file.getOriginalFilename())
                    .withMimeType(file.getContentType())
                    .withFileSize(file.getSize())
                    .isPublic(false)
                    .build();

            StorageOutput output = this.storageService.upload(input);
            conteudo.setKey(output.getKey());
            conteudo.setUrl(output.getUrl());
            conteudo.setMimetype(output.getMimeType());
        }

        this.conteudoRepository.save(conteudo);
    }

    public List<AulaResponse> buscarPorParametros(String titulo, UUID cursoId, UUID disciplinaId) {

        String sql = getSqlBase();
        sql += " where 1=1 ";

        if (titulo.isEmpty())
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
            String url = this.storageService.getLink(anexo.getKey());
            content.setUrl(url);
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
        String sql = "select a.id as id, a.titulo as titulo, c.titulo as curso, d.nome, d.id as disciplina_id ";
            sql += "as disciplina from ";
            sql += "aulas a inner join cursos c on a.curso_id = c.id ";
            sql += "inner join disciplinas d on a.disciplina_id = d.id ";
        return sql;
    }

    public UUID findCursoIdByFileId(String fileId) {
        return this.conteudoRepository.findCursoIdByFileId(fileId);
    }

}
