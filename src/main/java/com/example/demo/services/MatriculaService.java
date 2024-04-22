package com.example.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Matricula;
import com.example.demo.domain.Status;
import com.example.demo.domain.StatusPagamento;
import com.example.demo.domain.TipoProduto;
import com.example.demo.domain.Transacao;
import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.MatriculaAtivaResponse;
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
        
        Object produto = this.cursoService.findById(matricula.getProdutoId());
        if (produto == null) {
            produto = this.simuladoService.findById(matricula.getProdutoId());
        }

        if (produto == null)
            throw new IllegalArgumentException("Produto (curso/simulado) não inválido");

        Transacao transacao = new Transacao();
        transacao.setData(LocalDate.now());

        Matricula novaMatricula = new Matricula();
        
        novaMatricula.setUsuarioId(usuario.getId());
        // if (produto instanceof CursoDTO) {
        //     novaMatricula.setProdutoId(((CursoDTO) produto).getId());
        //     transacao.setDescricao("Matrícula em: " + ((CursoDTO) produto).getTitulo());
        //     transacao.setValor(((CursoDTO) produto).getValor());
        //     novaMatricula.setTipoProduto(TipoProduto.CURSO);
        // } else {
        //     novaMatricula.setProdutoId(((SimuladoDTO) produto).getId());
        //     transacao.setDescricao("Matrícula em: " + ((SimuladoDTO) produto).getTitulo());
        //     transacao.setValor(((SimuladoDTO) produto).getValor());
        //     novaMatricula.setTipoProduto(TipoProduto.SIMULADO);
        // }

        transacao = transacaoService.criarTransacao(transacao);

        novaMatricula.setStatus(transacao.getStatus() 
            == StatusPagamento.PAGO ? Status.ATIVO : Status.INATIVO);
        novaMatricula.setTransacaoId(transacao.getId());
        novaMatricula.setUsuarioId(usuario.getId());
        
        return matriculaRepository.save(novaMatricula).getId();
    }

    public List<Matricula> findByUsuarioId(UUID usuarioId) {
        return matriculaRepository.findByUsuarioIdAndStatus(usuarioId, Status.ATIVO);
    }

    public List<MatriculaAtivaResponse> getMatriculasAtivas(UUID alunoId) {
        
        List<Matricula> matriculas = this.findByUsuarioId(alunoId);
        
        List<MatriculaAtivaResponse> matriculasAtivas =
            new ArrayList<MatriculaAtivaResponse>();

        if (matriculas == null || matriculas.isEmpty())
            return matriculasAtivas;

        for (Matricula matricula : matriculas) {
            if (matricula.getTipoProduto() 
                == TipoProduto.CURSO) {
                matriculasAtivas.add(
                    this.cursoService.getMatriculaCursoResponse(
                        matricula.getProdutoId()));
                UUID simuladoId = this.simuladoService.findIdByCursoId(matricula.getProdutoId());
                if (simuladoId != null) {
                    matriculasAtivas.add(
                        this.simuladoService.getMatriculaSimulado(simuladoId));
                }
            } else {
                matriculasAtivas.add(
                    this.simuladoService.getMatriculaSimulado(
                        matricula.getProdutoId()));
            }
        }

        return matriculasAtivas;
    }

}
