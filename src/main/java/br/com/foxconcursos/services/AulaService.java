package br.com.foxconcursos.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

    public void atualizarAula(UUID id, AulaRequest request) {
        request.validate();

        Aula aula = this.repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Aula: '" + id + "' não encontrada."));
        
        aula.updateFromRequest(request);

        this.repository.save(aula);
    }

    @Transactional
    public UUID criarConteudo(UUID aulaId, AulaConteudoRequest request) 
            throws IOException, GeneralSecurityException {
        
        request.validate(false);
        
        AulaConteudo conteudo = request.toModel();
        conteudo.setAulaId(aulaId);

        MultipartFile file = request.getFile();

        String destino = "";

        switch (request.getTipo()){
            case VIDEO -> destino = "videos/";
            case APOSTILA -> destino = "apostilas/";
        }

        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(destino + file.getOriginalFilename())
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
            throws IOException, GeneralSecurityException {
        
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

    public List<AulaResponse> buscarPorParametros(String titulo, UUID cursoId, UUID disciplinaId, UUID assuntoId) {

        String sql = getSqlBase();
        sql += " where 1=1 ";

        if (titulo.isEmpty())
            sql += " and a.titulo like '%" + titulo + "%' ";
        
        if (!cursoId.equals(UUID.fromString(NULL_VALUE)))
            sql += " and c.id = '" + cursoId + "' ";

        if (!disciplinaId.equals(UUID.fromString(NULL_VALUE)))
            sql += " and d.id = '" + disciplinaId + "' ";

        if (!assuntoId.equals(UUID.fromString(NULL_VALUE)))
            sql += " and ass.id = '" + assuntoId + "' ";

        List<AulaResponse> response = new ArrayList<>();

        jdbcTemplate.query(sql, (rs) -> {
            AulaResponse aula = new AulaResponse();
            aula.setId(rs.getObject("id", UUID.class));
            aula.setTitulo(rs.getString("titulo"));
            aula.setCurso(rs.getString("curso"));
            aula.setDisciplina(rs.getString("disciplina"));
            aula.setAssunto(rs.getString("assunto"));
            response.add(aula);
        });

        return response;
    }

    public AulaResponse buscarPorId(UUID id) {
        String sql = getSqlBase();
        sql += " where a.id = '" + id + "'";

        AulaResponse response = jdbcTemplate.query(sql, (rs) -> {
            AulaResponse aula = new AulaResponse();
            aula.setId(rs.getObject("id", UUID.class));
            aula.setTitulo(rs.getString("titulo"));
            aula.setCurso(rs.getString("curso"));
            aula.setDisciplina(rs.getString("disciplina"));
            aula.setAssunto(rs.getString("assunto"));
            return aula;
        });

        List<AulaConteudo> anexos = this.conteudoRepository.findByAulaId(id);
        List<ConteudoResponse> conteudoResponse = new ArrayList<>();
        for (AulaConteudo anexo : anexos) {
            conteudoResponse.add(anexo.toAssembly());
        }

        response.setConteudo(conteudoResponse);

        return response;
    }

    private String getSqlBase() {
        String sql = "select a.id as id, a.titulo as titulo, c.titulo as curso, d.nome ";
            sql += "as disciplina, ass.nome as assunto from ";
            sql += "aulas a inner join cursos c on a.curso_id = c.id ";
            sql += "inner join disciplinas d on a.disciplina_id = d.id ";
            sql += "inner join assunto ass on a.assunto_id = ass.id ";
        return sql;
    }

    public UUID findCursoIdByFileId(String fileId) {
        return this.conteudoRepository.findCursoIdByFileId(fileId);
    }

}
