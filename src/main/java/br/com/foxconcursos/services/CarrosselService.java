package br.com.foxconcursos.services;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.domain.Carrossel;
import br.com.foxconcursos.dto.CarrosselRequest;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.CarrosselRepository;

@Service
public class CarrosselService {
    
    private final CarrosselRepository carrosselRepository;
    private final StorageService storageService;

    public CarrosselService(CarrosselRepository carrosselRepository, StorageService storageService) {
        this.carrosselRepository = carrosselRepository;
        this.storageService = storageService;
    }

    public List<Carrossel> findAll() {
        return carrosselRepository.findAll()
            .stream()
            .sorted(Comparator.comparingInt(Carrossel::getOrdem))
            .toList();
    }

    public Carrossel findById(UUID id) {
        return carrosselRepository.findById(id).orElseThrow(
            () -> new IllegalStateException("Carrossel não encontrado"));
    }

    public void deleteById(UUID id) {
        carrosselRepository.deleteById(id);
    }

    public Carrossel save(CarrosselRequest request) throws Exception {
        
        if (request.getOrdem() < 0) {
            throw new IllegalArgumentException("Ordem deve ser maior ou igual a zero");
        }

        MultipartFile file = request.getImagem();

        if (Objects.isNull(file)) {
            throw new IllegalArgumentException("Imagem é obrigatória");
        }

        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(file.getOriginalFilename())
                .withFileSize(file.getSize())
                .withMimeType(file.getContentType())
                .isPublic(true)
                .build();
        
        StorageOutput output = storageService.upload(input);
        

        Carrossel carrossel = new Carrossel();
        carrossel.setOrdem(request.getOrdem());
        carrossel.setImagem(output.getUrl()); // Assuming StorageOutput has a method getUrl()
        carrossel.setLink(request.getLink());

        return carrosselRepository.save(carrossel);
    }

    public void update(UUID id, CarrosselRequest request) throws Exception {
        Carrossel carrossel = this.findById(id);

        if (request.getOrdem() < 0) {
            throw new IllegalArgumentException("Ordem deve ser maior ou igual a zero");
        }

        MultipartFile file = request.getImagem();

        if (Objects.nonNull(file)) {
            StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(file.getOriginalFilename())
                .withFileSize(file.getSize())
                .withMimeType(file.getContentType())
                .isPublic(true)
                .build(); 
            
            StorageOutput output = storageService.upload(input);
            carrossel.setImagem(output.getUrl());
        }        

        carrossel.setOrdem(request.getOrdem());
        carrossel.setLink(request.getLink());

        carrosselRepository.save(carrossel);
    }
    
}
