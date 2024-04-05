package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Disciplina;
import com.example.demo.repositories.DisciplinaRepository;

@Service
public class DisciplinaService {
    
    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    public Disciplina salvar(Disciplina disciplina) {
        if (disciplina == null || disciplina.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da disciplina.");
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }
}
