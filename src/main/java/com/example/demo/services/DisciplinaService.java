package com.example.demo.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.domain.CursoDisciplina;
import com.example.demo.domain.Disciplina;
import com.example.demo.repositories.CursoDisciplinaRepository;
import com.example.demo.repositories.DisciplinaRepository;

@Service
public class DisciplinaService {
    
    private final DisciplinaRepository disciplinaRepository;
    private final CursoDisciplinaRepository cursoDisciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, 
        CursoDisciplinaRepository cursoDisciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoDisciplinaRepository = cursoDisciplinaRepository;
    }

    public Disciplina salvar(Disciplina disciplina) {
        if (disciplina == null || disciplina.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da disciplina.");
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }

    public List<Disciplina> findByCursoId(UUID cursoId) {
        Map<UUID, Disciplina> disciplinas = disciplinaRepository.findAll().stream()
            .collect(Collectors.toMap(Disciplina::getId, d -> d));
        
        List<CursoDisciplina> disciplinaIds = cursoDisciplinaRepository.findByCursoId(cursoId);
        return disciplinaIds.stream()
            .map(d -> disciplinas.get(d.getDisciplinaId()))
            .collect(Collectors.toList());
    }

    public String adicionarDisciplina(CursoDisciplina cursoDisciplina) {
        if (cursoDisciplina == null || cursoDisciplina.getCursoId() == null 
            || cursoDisciplina.getDisciplinaId() == null)
            throw new IllegalArgumentException("Informe o curso e a disciplina.");
        cursoDisciplina = cursoDisciplinaRepository.save(cursoDisciplina);
        return cursoDisciplina.getDisciplinaId() + "/" + cursoDisciplina.getCursoId();
    }

    public void removerDisciplina(CursoDisciplina cursoDisciplina) {
        cursoDisciplinaRepository.delete(cursoDisciplina);
    }

    public Disciplina findById(UUID disciplinaId) {
        return disciplinaRepository.findById(disciplinaId)
            .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));
    }

    public boolean existsByCursoId(UUID cursoId) {
        return cursoDisciplinaRepository.existsByCursoId(cursoId);
    }
}
