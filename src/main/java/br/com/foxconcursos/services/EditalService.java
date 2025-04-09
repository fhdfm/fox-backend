package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Edital;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.dto.EditalRequest;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.EditalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EditalService {

    private final EditalRepository repository;
    private final StorageService storageService;

    public EditalService(EditalRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    @Transactional
    public UUID create(EditalRequest request) throws Exception {

        if (Objects.isNull(request.getTitulo()))
            throw new IllegalArgumentException("Título é obrigatório.");

        if (Objects.isNull(request.getArquivo()))
            throw new IllegalArgumentException("Arquivo é obrigatório.");

        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(request.getArquivo().getInputStream())
                .withFileName(request.getArquivo().getOriginalFilename())
                .withMimeType(request.getArquivo().getContentType())
                .withFileSize(request.getArquivo().getSize())
                .isPublic(true)
                .build();

        StorageOutput output = this.storageService.upload(input);

        Edital edital = new Edital();
        edital.setAno(request.getAno());
        edital.setLink(output.getUrl());
        edital.setCidade(request.getCidade());
        edital.setUf(request.getUf());
        edital.setTitulo(request.getTitulo());
        edital.setStatus(Status.ATIVO);

        this.repository.save(edital);

        return edital.getId();
    }

    @Transactional
    public void update(EditalRequest request, UUID id) throws Exception {

        if (Objects.isNull(request.getTitulo())) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }

        Edital edital = this.findById(id);

        if (Objects.nonNull(request.getArquivo())) {
            StorageInput input = new StorageInput.Builder()
                    .withFileInputStream(request.getArquivo().getInputStream())
                    .withFileName(request.getArquivo().getOriginalFilename())
                    .withMimeType(request.getArquivo().getContentType())
                    .withFileSize(request.getArquivo().getSize())
                    .isPublic(true)
                    .build();

            StorageOutput output = this.storageService.upload(input);
            edital.setLink(output.getUrl());
        }

        edital.setAno(request.getAno());
        edital.setTitulo(request.getTitulo());
        edital.setCidade(request.getCidade());
        edital.setUf(request.getUf());

        this.repository.save(edital);
    }

    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

    public Edital findById(UUID id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Edital> findAll() {


        return this.repository.findAllOrderByAnoDesc();

    }

    public List<Edital> findAllPublic() {

        return this.repository.findAllAtivosOrderByAnoDesc();

    }

    public void activate(UUID id) {
        Edital edital = this.repository.findById(id).orElseThrow(() -> new IllegalStateException(
                "Edital não encontrado com o id: " + id));

        edital.setStatus(Status.ATIVO);
        this.repository.save(edital);
    }

    public void deactivate(UUID id) {
        Edital edital = this.repository.findById(id).orElseThrow(() -> new IllegalStateException(
                "Edital não encontrado com o id: " + id));

        edital.setStatus(Status.INATIVO);
        this.repository.save(edital);
    }

}
