package br.com.foxconcursos.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Aula;
import br.com.foxconcursos.domain.AulaConteudo;
import br.com.foxconcursos.dto.AulaConteudoRequest;
import br.com.foxconcursos.dto.AulaRequest;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.AulaConteudoRepository;
import br.com.foxconcursos.repositories.AulaRepository;

@Service
public class AulaService {
    
    private final AulaRepository repository;
    private final AulaConteudoRepository conteudoRepository;
    private final StorageService storageService;

    public AulaService(AulaRepository repository, 
            AulaConteudoRepository conteudoRepository, StorageService storageService) {
        
        this.repository = repository;
        this.conteudoRepository = conteudoRepository;
        this.storageService = storageService;

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

        StorageInput input = new StorageInput.Builder()
            .withInputStream(request.getFile())
            .isPublic(false)
            .build();
        
        StorageOutput output = this.storageService.upload(input);

        if (input.isMovie()) {
            conteudo.setVideo(output.getVideoUrl());
            conteudo.setThumbnail(output.getThumbnailUrl());
        } else {
            conteudo.setFileId(output.getFileId());
        }

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
            
            StorageInput input = new StorageInput.Builder()
                    .withInputStream(request.getFile())
                    .isPublic(false)
                    .build();

            StorageOutput output = this.storageService.upload(input);
            if (input.isMovie()) {
                conteudo.setVideo(output.getVideoUrl());
                conteudo.setThumbnail(output.getThumbnailUrl());
            } else {
                conteudo.setFileId(output.getFileId());
            }
        }

        this.conteudoRepository.save(conteudo);
    }

}
