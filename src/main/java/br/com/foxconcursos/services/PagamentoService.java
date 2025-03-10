package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.*;
import br.com.foxconcursos.dto.ProdutoMercadoPagoRequest;
import br.com.foxconcursos.repositories.PagamentoRepository;
import br.com.foxconcursos.repositories.UsuarioRepository;
import br.com.foxconcursos.util.SecurityUtil;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final MatriculaService matriculaService;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoService enderecoService;
    private final EmailService emailService;


    public PagamentoService(
            PagamentoRepository repository,
            MatriculaService matriculaService,
            UsuarioRepository usuarioRepository,
            EnderecoService enderecoService,
            EmailService emailService
    ) {
        this.repository = repository;
        this.matriculaService = matriculaService;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.enderecoService = enderecoService;
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

    public boolean existsPagamento(String dataId){
        return repository.existsByMpId(dataId);
    }


}
