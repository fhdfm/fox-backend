package br.com.foxconcursos.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Matricula;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.domain.StatusPagamento;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.domain.Transacao;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.dto.ProdutoResponse;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.UsuarioResponse;
import br.com.foxconcursos.repositories.MatriculaRepository;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;

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
    public UUID matricularContingencia(MatriculaRequest matricula) {

        if (matricula.getUsuarioId() == null)
            throw new IllegalArgumentException("Usuário não informado");
        
        UsuarioResponse usuario = this.usuarioService.findById(matricula.getUsuarioId());
            
        if (matricula.getProdutoId() == null)
            throw new IllegalArgumentException("Produto (curso/simulado) não informado");
        
        Object produto = this.cursoService.obterPorId(matricula.getProdutoId());
        if (produto == null) {
            produto = this.simuladoService.findById(matricula.getProdutoId());
        }

        if (produto == null)
            throw new IllegalArgumentException("Produto (curso/simulado) não inválido");

        Transacao transacao = new Transacao();
        transacao.setData(LocalDate.now());

        Matricula novaMatricula = new Matricula();
        
        novaMatricula.setUsuarioId(usuario.getId());
        if (produto instanceof Curso) {
            novaMatricula.setProdutoId(((Curso) produto).getId());
            transacao.setDescricao("Matrícula em: " + ((Curso) produto).getTitulo());
            transacao.setValor(((Curso) produto).getValor());
            novaMatricula.setTipoProduto(TipoProduto.CURSO);
        } else {
            novaMatricula.setProdutoId(((SimuladoCompletoResponse) produto).getId());
            transacao.setDescricao("Matrícula em: " + ((SimuladoCompletoResponse) produto).getTitulo());
            transacao.setValor(((SimuladoCompletoResponse) produto).getValor());
            novaMatricula.setTipoProduto(TipoProduto.SIMULADO);
        }

        transacao = transacaoService.criarTransacao(transacao);

        novaMatricula.setStatus(transacao.getStatus() 
            == StatusPagamento.PAGO ? Status.ATIVO : Status.INATIVO);
        novaMatricula.setTransacaoId(transacao.getId());
        novaMatricula.setUsuarioId(usuario.getId());
        
        return matriculaRepository.save(novaMatricula).getId();
    }

    @Transactional
    public UUID matricular(MatriculaRequest matricula) {

        if (matricula.getUsuarioId() == null)
            throw new IllegalArgumentException("Usuário não informado");
        
        UsuarioResponse usuario = this.usuarioService.findById(matricula.getUsuarioId());
            
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

    public List<ProdutoResponse> getMatriculasAtivas(UUID alunoId) {
        
        List<Matricula> matriculas = this.findByUsuarioId(alunoId);
        
        List<ProdutoResponse> matriculasAtivas =
            new ArrayList<ProdutoResponse>();

        if (matriculas == null || matriculas.isEmpty())
            return matriculasAtivas;

        for (Matricula matricula : matriculas) {
            if (matricula.getTipoProduto() 
                == TipoProduto.CURSO) {
                matriculasAtivas.add(
                    this.cursoService.getMatriculaCursoResponse(
                        matricula.getProdutoId()));
                if (simuladoService.existsByCursoId(matricula.getProdutoId())) {
                    List<Simulado> simulados = this.simuladoService.findByCursoId(matricula.getProdutoId());
                    if (simulados != null) {
                        for (Simulado simulado : simulados) {
                            matriculasAtivas.add(
                                this.simuladoService.getMatriculaSimulado(simulado.getId()));
                        }
                    }
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
