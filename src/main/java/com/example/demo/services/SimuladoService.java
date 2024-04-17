package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Simulado;
import com.example.demo.dto.CursoDTO;
import com.example.demo.dto.SimuladoDTO;
import com.example.demo.repositories.SimuladoRepository;

@Service
public class SimuladoService {
    
    private final SimuladoRepository simuladoRepository;
    private final CursoService cursoService;

    public SimuladoService(SimuladoRepository simuladoRepository, CursoService cursoService) {
        this.simuladoRepository = simuladoRepository;
        this.cursoService = cursoService;
    }

    public UUID save(SimuladoDTO simuladoDTO) {
        Simulado simulado = new Simulado(simuladoDTO);
        /* TODO: Implementar regras de validação.... */
        simulado = simuladoRepository.save(simulado);
        return simulado.getId();
    }

    public SimuladoDTO findById(UUID id) {
        Simulado simulado = simuladoRepository.findById(id).orElse(null);
        SimuladoDTO simuladoDTO = new SimuladoDTO(simulado);
        CursoDTO curso = cursoService.findById(simulado.getCursoId());
        simuladoDTO.setNomeCurso(curso.getTitulo());
        simuladoDTO.setBancaId(curso.getBancaId());
        simuladoDTO.setNomeBanca(curso.getNomeBanca());
        return simuladoDTO;
    }

    public void delete(UUID id) {
        simuladoRepository.deleteById(id);
    }

    public List<SimuladoDTO> findAll() {
        List<Simulado> simulados = simuladoRepository.findAll();
        Map<UUID, CursoDTO> cursos = cursoService.findAllAsMap();
        List<SimuladoDTO> result = new ArrayList<SimuladoDTO>();
        for (Simulado simulado : simulados) {
            SimuladoDTO simuladoDTO = new SimuladoDTO(simulado);
            CursoDTO curso = cursos.get(simuladoDTO.getCursoId());
            simuladoDTO.setNomeCurso(curso.getTitulo());
            simuladoDTO.setBancaId(curso.getBancaId());
            simuladoDTO.setNomeBanca(curso.getNomeBanca());
            result.add(simuladoDTO);
        }
        return result;
    }

}
