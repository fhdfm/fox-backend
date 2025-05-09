package br.com.foxconcursos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import br.com.foxconcursos.domain.Endereco;
import br.com.foxconcursos.domain.Pagamento;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.ProdutoMercadoPagoRequest;
import br.com.foxconcursos.dto.VendasFilterResquest;
import br.com.foxconcursos.dto.VendasResponse;
import br.com.foxconcursos.dto.VendasStatusUpdateRequest;
import br.com.foxconcursos.repositories.PagamentoRepository;
import br.com.foxconcursos.repositories.UsuarioRepository;
import br.com.foxconcursos.repositories.VendasRowMapper;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final MatriculaService matriculaService;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoService enderecoService;
    private final EmailService emailService;
    private final JdbcTemplate jdbcTemplate;


    public PagamentoService(
            PagamentoRepository repository,
            MatriculaService matriculaService,
            UsuarioRepository usuarioRepository,
            EnderecoService enderecoService, EmailService emailService,
            JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.matriculaService = matriculaService;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.enderecoService = enderecoService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String registrarPreCompra(ProdutoMercadoPagoRequest produto, Usuario usuario) {
        Pagamento pagamento = new Pagamento();
        pagamento.setTipo(produto.getTipo());
        pagamento.setTelefone(usuario.getTelefone());

        if (produto.getTipo().equals(TipoProduto.QUESTOES)) {
            pagamento.setProdutoId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            pagamento.setPeriodo(produto.getPeriodo());
            pagamento.setTitulo("Banco de questões: " + produto.getPeriodo() + " mês(es)");
        } else {
            if (produto.getTipo().equals(TipoProduto.APOSTILA) && produto.getEndereco() != null) {
                produto.getEndereco().setUsuarioId(usuario.getId());
                pagamento.setParaEntrega(true);
                salvarEnderecoUsuario(produto.getEndereco());
            }
            pagamento.setTitulo(produto.getTitulo());
            pagamento.setProdutoId(produto.getUuid());
        }

        UsuarioLogado currentUser = SecurityUtil.obterUsuarioLogado();
        pagamento.setUsuarioId(currentUser.getId());

        this.repository.save(pagamento);

        return pagamento.getId().toString();
    }

    private void salvarEnderecoUsuario(Endereco enderecoRequest) {
        enderecoService.salvar(enderecoRequest);
    }

    @Transactional
    public void update(Pagamento pagamento) {
        Pagamento payment = this.repository.findById(pagamento.getId()).orElse(null);

        if (payment == null) {
            payment = new Pagamento();
        }

        payment.setStatus(pagamento.getStatus());
        payment.setMpId(pagamento.getMpId());


        this.repository.save(payment);

    }

    public void enviarEmail(Pagamento pagamentoRequest) {
        Usuario usuario = usuarioRepository.findById(pagamentoRequest.getUsuarioId()).orElse(null);
        Pagamento pagamento = repository.findById(pagamentoRequest.getId()).orElse(null);

        if (usuario == null || pagamento == null) {
            throw new RuntimeException("Dados de pagamento não encontrados para o ID: " + pagamentoRequest.getId());
        }

        Endereco endereco = null;

        if (pagamentoRequest.getTipo().equals(TipoProduto.APOSTILA)) {
            endereco = enderecoService.buscarPorId(pagamentoRequest.getUsuarioId());
        }

//        emailService.enviarEmailPagamentoAprovado(usuario.getEmail(), usuario.getNome(), pagamento.getTitulo(), pagamento.getTipo(), endereco);
    }

    public String createPayment(ProdutoMercadoPagoRequest produto) {
        try {
            Usuario usuario = usuarioRepository.findById(SecurityUtil.obterUsuarioLogado().getId()).orElse(null);
            PreferenceClient client = new PreferenceClient();

            PreferenceItemRequest itemProduto = PreferenceItemRequest.builder()
                    .id(produto.getUuid().toString())
                    .title(produto.getTitulo())
                    .description(produto.getTitulo())
                    .quantity(1)
                    .categoryId("services")
                    .unitPrice(BigDecimal.valueOf(produto.getValor()))
                    .build();

            PreferenceItemRequest itemFrete = PreferenceItemRequest.builder()
                    .title("Taxa de Frete")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(produto.getTaxaFrete()))
                    .build();

            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .name(usuario.getNome())
                    .email(usuario.getEmail())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Arrays.asList(itemProduto, itemFrete))
                    .payer(payerRequest)
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("https://www.foxconcursos.com.br/auth")
                            .failure("https://www.foxconcursos.com.br/auth")
                            .pending("https://www.foxconcursos.com.br/auth")
                            .build()
                    )
                    .autoReturn("approved")
                    .statementDescriptor("Fox Cursos: " + produto.getTitulo())
                    .notificationUrl("https://fox-backend.onrender.com/mp")
                    .externalReference(registrarPreCompra(produto, usuario))
                    .build();

            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao criar pagamento";
        }
    }

    public boolean existsPagamento(String dataId) {
        return repository.existsByMpId(dataId);
    }

    public Page<VendasResponse> findByParameters(VendasFilterResquest request, Pageable pageable) {

        String sqlBase = """
                    select v.id, v.usuario_id, u.nome, v.produto_id, v.mp_id, v.data, v.periodo, 
                           v.tipo, v.titulo, v.para_entrega, v.produto_enviado, v.telefone, 
                           e.logradouro, e.numero, e.complemento, e.bairro, e.cidade, e.estado, e.cep  
                    from mercado_pago v 
                    inner join usuarios u on u.id = v.usuario_id
                    inner join enderecos e on e.usuario_id = v.usuario_id 
                    where v.status = 'approved'
                """;

        StringBuilder sql = new StringBuilder(sqlBase);
        List<Object> params = new ArrayList<>();

        String nomeUsuario = request.getNomeUsuario();
        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            sql.append(" and LOWER(u.nome) LIKE LOWER(?)");
            params.add("%" + nomeUsuario + "%"); // Pesquisa parcial no nome do usuário
        }

        UUID produtoId = request.getProduto();
        if (produtoId != null) {
            sql.append(" and v.produto_id = ?");
            params.add(produtoId);
        }

        LocalDateTime dataInicio = request.getDataInicio();
        if (dataInicio != null) {
            sql.append(" and v.data >= ?");
            params.add(dataInicio);
        }

        LocalDateTime dataFim = request.getDataFim();
        if (dataFim != null) {
            sql.append(" and v.data <= ?");
            params.add(dataFim);
        }

        TipoProduto tipo = request.getTipo();
        if (tipo != null) {
            sql.append(" and v.tipo = ?");
            params.add(tipo.name());

            if (tipo.equals(TipoProduto.APOSTILA)) {
                Boolean entrega = request.isEntrega();
                if (entrega != null) {
                    sql.append(" and v.para_entrega = ?");
                    params.add(entrega);
                }

                Boolean enviado = request.isEnviado();
                if (enviado != null) {
                    sql.append(" and v.produto_enviado = ?");
                    params.add(enviado);
                }
            }
        }

        String orderClause = "";
        if (pageable.getSort().isSorted()) {
            orderClause = " order by ";
            List<String> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                orders.add(order.getProperty() + " " + order.getDirection().name());
            });
            orderClause += String.join(", ", orders);
        }
        sql.append(" ORDER BY v.data DESC");
        String paginatedQuery = sql.toString() + orderClause + " limit ? offset ?";
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<VendasResponse> vendas = jdbcTemplate.query(
                paginatedQuery,
                new VendasRowMapper(),
                params.toArray());

        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from mercado_pago v inner join usuarios u on u.id = v.usuario_id inner join enderecos e on e.usuario_id = v.usuario_id where v.status = 'approved'");

        List<Object> countParams = new ArrayList<>();

        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            sqlCountQuery.append(" and LOWER(u.nome) LIKE LOWER(?)");
            countParams.add("%" + nomeUsuario + "%");
        }

        if (produtoId != null) {
            sqlCountQuery.append(" and v.produto_id = ?");
            countParams.add(produtoId);
        }

        if (dataInicio != null) {
            sqlCountQuery.append(" and v.data >= ?");
            countParams.add(dataInicio);
        }

        if (dataFim != null) {
            sqlCountQuery.append(" and v.data <= ?");
            countParams.add(dataFim);
        }

        if (tipo != null) {
            sqlCountQuery.append(" and v.tipo = ?");
            countParams.add(tipo.name());

            if (tipo.equals(TipoProduto.APOSTILA)) {
                Boolean entrega = request.isEntrega();
                if (entrega != null) {
                    sqlCountQuery.append(" and v.para_entrega = ?");
                    countParams.add(entrega);
                }

                Boolean enviado = request.isEnviado();
                if (enviado != null) {
                    sqlCountQuery.append(" and v.produto_enviado = ?");
                    countParams.add(enviado);
                }
            }
        }

        Integer total = jdbcTemplate.queryForObject(sqlCountQuery.toString(), Integer.class, countParams.toArray());

        return new PageImpl<>(vendas, pageable, total);
    }

    @Transactional
    public void atualizarStatusVenda(VendasStatusUpdateRequest request) {
        Pagamento pagamento = repository.findById(request.getVendaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Venda com o ID: " + request.getVendaId() + " não foi encontrada."));

        pagamento.setProdutoEnviado(request.getEnviado());

        repository.save(pagamento);
    }


}
