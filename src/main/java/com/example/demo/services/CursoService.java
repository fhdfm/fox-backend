package com.example.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Banca;
import com.example.demo.domain.Curso;
import com.example.demo.dto.CursoDTO;
import com.example.demo.repositories.CursoRepository;

@Service
public class CursoService {
    
    // private final StorageService storageService;

    private final CursoRepository cursoRepository;

    private final BancaService bancaService;

    public CursoService(CursoRepository cursoRepository, BancaService bancaService) {
        this.cursoRepository = cursoRepository;
        this.bancaService = bancaService;
    }

    public UUID save(CursoDTO cursoDTO) {
        
        // byte[] imageBytes = cursoDTO.getImageBytes();
        // if (imageBytes != null) {
        //     String imageUrl = storageService.upload(imageBytes);
        //     cursoDTO.setImage(imageUrl);
        // }

        validarEntrada(cursoDTO);

        return this.cursoRepository.save(new Curso(cursoDTO)).getId();
    }

    private void validarEntrada(CursoDTO cursoDTO) {
        if (cursoDTO.getTitulo() == null) {
            throw new IllegalArgumentException("Título é requerido.");
        }

        if (cursoDTO.getDescricao() == null) {
            throw new IllegalArgumentException("Descrição é requerida.");
        }

        if (cursoDTO.getValor() == null) {
            throw new IllegalArgumentException("Preço é requerido.");
        }
    }

    public void delete(UUID id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        if (!this.cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        this.cursoRepository.deleteById(id);
    }

    public CursoDTO findById(UUID id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        Curso curso = this.cursoRepository.findById(id).orElse(null);

        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        Banca banca = this.bancaService.findById(curso.getBancaId());

        CursoDTO result = new CursoDTO(curso);
        result.setNomeBanca(banca.getNome());

        return result;
    }

    public Page<CursoDTO> findAll(Pageable pageable) {

        Map<UUID, String> bancas = this.bancaService.findAllAsMap();

        Page<Curso> cursos = this.cursoRepository.findAll(pageable);
        Page<CursoDTO> result = cursos.map(new Function<Curso, CursoDTO>() {
            public CursoDTO apply(Curso curso) {
                String banca = bancas.get(curso.getBancaId());
                CursoDTO cursoDTO = new CursoDTO(curso);
                cursoDTO.setNomeBanca(banca);
                return cursoDTO;
            }
        });

        return result;
    }

    public Map<UUID, CursoDTO> findAllAsMap() {

        Map<UUID, CursoDTO> result = new HashMap<UUID, CursoDTO>();

        Map<UUID, String> bancas = this.bancaService.findAllAsMap();

        List<Curso> cursos = this.cursoRepository.findAll();
        for (Curso curso : cursos) {
            String banca = bancas.get(curso.getBancaId());
            CursoDTO cursoDTO = new CursoDTO(curso);
            cursoDTO.setNomeBanca(banca);
            result.put(curso.getId(), cursoDTO);
        }

        return result;
    }

}
