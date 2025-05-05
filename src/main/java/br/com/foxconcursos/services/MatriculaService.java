package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.*;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.dto.SimuladoCompletoResponse;
import br.com.foxconcursos.dto.UsuarioResponse;
import br.com.foxconcursos.events.MatriculaBancoQuestaoEvent;
import br.com.foxconcursos.repositories.MatriculaRepository;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final TransacaoService transacaoService;
    private final SimuladoService simuladoService;
    private final CursoService cursoService;
    private final UsuarioServiceImpl usuarioService;
    private final ApplicationEventPublisher publisher;
    private final JdbcTemplate jdbcTemplate;


    public MatriculaService(
            MatriculaRepository matriculaRepository,
            TransacaoService transacaoService,
            SimuladoService simuladoService,
            CursoService cursoService,
            UsuarioServiceImpl usuarioService,
            JdbcTemplate jdbcTemplate,
            ApplicationEventPublisher publisher
    ) {
        this.matriculaRepository = matriculaRepository;
        this.transacaoService = transacaoService;
        this.simuladoService = simuladoService;
        this.cursoService = cursoService;
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioService = usuarioService;
        this.publisher = publisher;
    }

    @Transactional
    public UUID matricular(MatriculaRequest matricula) {

        if (matricula.getUsuarioId() == null)
            throw new IllegalArgumentException("Usuário não informado");

        if (matricula.getProdutoId() == null)
            throw new IllegalArgumentException("Produto (curso/simulado) não informado");

        UsuarioResponse usuario = this.usuarioService.findById(matricula.getUsuarioId());

        Optional<Matricula> matriculaExistente = matriculaRepository.findByUsuarioIdAndProdutoId(
                matricula.getUsuarioId(), matricula.getProdutoId());

        if (matriculaExistente.isPresent()) {
            Matricula existente = matriculaExistente.get();

            if (existente.getStatus() == Status.INATIVO) {
                existente.setStatus(Status.ATIVO);
                matriculaRepository.save(existente);
                return existente.getId();
            } else {
                throw new IllegalStateException("Usuário já está matriculado neste produto.");
            }
        }

        if (matricula.isBancoQuestao())
            return matricularBancoQuestao(usuario, matricula);
        else
            return matricularCursoSimulado(usuario, matricula);
    }

    @Transactional
    public UUID desvincular(MatriculaRequest matricula) {
        if (matricula.getUsuarioId() == null || matricula.getProdutoId() == null) {
            throw new IllegalArgumentException("Usuário e produto devem ser informados.");
        }

        Matricula matriculaExistente = matriculaRepository
                .findByUsuarioIdAndProdutoId(matricula.getUsuarioId(), matricula.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        // Lógica de inativação (ao invés de deletar)
        matriculaExistente.setStatus(Status.INATIVO);
        matriculaRepository.save(matriculaExistente);

        return matriculaExistente.getId();
    }


    @Transactional
    private UUID matricularBancoQuestao(UsuarioResponse usuario, MatriculaRequest matricula) {

        LocalDateTime hoje = LocalDateTime.now();

        if (matricula.getValor() == null) {
            throw new IllegalArgumentException("Valor é obrigatório.");
        }

        if (matricula.getDataFim() == null) {
            throw new IllegalArgumentException("Data fim é obrigatória.");
        }

        System.out.println("Data fim:");
        System.out.println(matricula.getDataFim());
        System.out.println(hoje);

        if (hoje.isAfter(matricula.getDataFim())) {
            throw new IllegalArgumentException("Data Inicio não pode ser maior que Data Fim.");
        }

        Transacao transacao = new Transacao();
        transacao.setData(LocalDate.now());

        transacao.setDescricao("Matrícula em: Banco de Questões");
        transacao.setValor(matricula.getValor());

        Matricula novaMatricula = new Matricula();
        novaMatricula.setUsuarioId(usuario.getId());
        novaMatricula.setProdutoId(matricula.getProdutoId());
        novaMatricula.setTipoProduto(TipoProduto.QUESTOES);

        transacao = transacaoService.criarTransacao(transacao);

        novaMatricula.setStatus(transacao.getStatus()
                == StatusPagamento.PAGO ? Status.ATIVO : Status.INATIVO);
        novaMatricula.setTransacaoId(transacao.getId());
        novaMatricula.setUsuarioId(usuario.getId());

        UUID matriculaId = matriculaRepository.save(novaMatricula).getId();

        MatriculaBancoQuestaoEvent event =
                new MatriculaBancoQuestaoEvent(matriculaId, hoje, matricula.getDataFim());

        publisher.publishEvent(event);

        return matriculaId;
    }

    @Transactional
    private UUID matricularCursoSimulado(UsuarioResponse usuario, MatriculaRequest matricula) {

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

    public List<Matricula> findByUsuarioId(UUID usuarioId) {
        return matriculaRepository.findByUsuarioIdAndStatus(usuarioId, Status.ATIVO);
    }

    public Page<Usuario> buscarUsuariosPorProdutoId(UUID produtoId, Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        String sql = """
                    SELECT u.id, u.email, u.nome, u.cpf, u.perfil, u.telefone, u.status
                    FROM matriculas m
                    JOIN usuarios u ON m.usuario_id = u.id
                    WHERE m.produto_id = CAST(? AS UUID)
                    ORDER BY u.nome ASC
                    LIMIT ? OFFSET ?
                """;

        RowMapper<Usuario> rowMapper = (rs, rowNum) -> {
            Usuario usuario = new Usuario();
            usuario.setId(UUID.fromString(rs.getString("id")));
            usuario.setEmail(rs.getString("email"));
            usuario.setTelefone(rs.getString("telefone") != null ? rs.getString("telefone") : "");
            usuario.setNome(rs.getString("nome"));
            return usuario;
        };

        List<Usuario> usuarios = jdbcTemplate.query(sql, rowMapper, produtoId, size, offset);

        return new PageImpl<>(usuarios, pageable, contarMatriculasPorProdutoId(produtoId));
    }

    public int contarMatriculasPorProdutoId(UUID produtoId) {
        String sql = "SELECT COUNT(*) FROM matriculas WHERE produto_id = CAST(? AS UUID)";
        return jdbcTemplate.queryForObject(sql, Integer.class, produtoId);
    }
}
