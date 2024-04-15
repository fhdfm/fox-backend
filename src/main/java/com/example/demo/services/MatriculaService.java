package com.example.demo.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.MatriculaRequest;
import com.example.demo.repositories.MatriculaRepository;
import com.example.demo.services.impl.UsuarioServiceImpl;

@Service
public class MatriculaService {
    
    private final MatriculaRepository matriculaRepository;
    private final TransacaoService transacaoService;
    private final SimuladoService simuladoService;
    private final CursoService cursoService;
    private final UsuarioServiceImpl usuarioService;

    public MatriculaService(MatriculaRepository matriculaRepository, 
        TransacaoService transacaoService, SimuladoService simuladoService, 
        CursoService cursoService, UsuarioServiceImpl usuarioService) {
        this.matriculaRepository = matriculaRepository;
        this.transacaoService = transacaoService;
        this.simuladoService = simuladoService;
        this.cursoService = cursoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public UUID matricular(MatriculaRequest matricula) {

        if (matricula.getUsuarioId() == null)
            throw new IllegalArgumentException("Usuário não informado");
        
        UsuarioLogado usuario = this.usuarioService.findById(matricula.getUsuarioId());
            
        if (matricula.getProdutoId() == null)
            throw new IllegalArgumentException("Produto (curso/simulado) não informado");
        
        // try {
        //     Curso curso = this.cursoService.findById(matricula.getProdutoId());
        // } catch (Exception e) {
        // }
        
        return null;

    }

}
