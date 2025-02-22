package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Pagamento;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.dto.ProdutoMercadoPagoRequest;
import br.com.foxconcursos.repositories.ApostilaRepository;
import br.com.foxconcursos.repositories.CursoRepository;
import br.com.foxconcursos.repositories.PagamentoRepository;
import br.com.foxconcursos.repositories.UsuarioRepository;
import br.com.foxconcursos.util.SecurityUtil;
import com.mercadopago.client.common.AddressRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    private final MatriculaService matriculaService;

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApostilaRepository apostilaRepository;

    public PagamentoService(
            PagamentoRepository repository,
            MatriculaService matriculaService,
            UsuarioRepository usuarioRepository ,
            CursoRepository cursoRepository,
            ApostilaRepository apostilaRepository
    ) {
        this.repository = repository;
        this.matriculaService = matriculaService;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.apostilaRepository = apostilaRepository;
    }

    public String registrarPreCompra(ProdutoMercadoPagoRequest produto) {
        Pagamento pagamento = new Pagamento();
        pagamento.setProdutoId(produto.getUuid());
        pagamento.setTipo(produto.getTipo());

        if(produto.getTipo().equals(TipoProduto.QUESTOES)){
            pagamento.setPeriodo(produto.getPeriodo());
        }

        UsuarioLogado currentUser = SecurityUtil.obterUsuarioLogado();

        pagamento.setUsuarioId(currentUser.getId());

        this.repository.save(pagamento);

        return pagamento.getId().toString();
    }

    @Transactional
    public void update(Pagamento pagamento) {
        Pagamento payment = this.repository.findById(pagamento.getId()).get();
        payment.setStatus(pagamento.getStatus());
        payment.setData(pagamento.getData());
        payment.setMpId(pagamento.getMpId());
        this.repository.save(payment);

        if (payment.isAprovado()) {
            MatriculaRequest matriculaRequest = new MatriculaRequest();
            matriculaRequest.setProdutoId(payment.getProdutoId());
            matriculaRequest.setUsuarioId(payment.getUsuarioId());
            matriculaRequest.setValor(pagamento.getValor());
            if (payment.getTipo().equals(TipoProduto.QUESTOES))
                matriculaRequest.setDataFim(LocalDateTime.now().plusMonths(pagamento.getPeriodo()));
            this.matriculaService.matricular(matriculaRequest);
        }
    }

    public String pagar(ProdutoMercadoPagoRequest produto) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(SecurityUtil.obterUsuarioLogado().getId());

            PreferenceClient client = new PreferenceClient();

            // CASO PRECISE ---------------------------------------
//            PreferenceReceiverAddressRequest addressRequest = PreferenceReceiverAddressRequest.builder()
//                    .streetName("")
//                    .cityName("")
//                    .streetNumber("")
//                    .build();
//            PreferenceShipmentsRequest preferenceShipmentsRequest =  PreferenceShipmentsRequest.builder()
//                    .receiverAddress(addressRequest)
//                    .build();
            // CASO PRECISE ---------------------------------------

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(produto.getTitulo())
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(produto.getValor()))
                    .build();

            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .email(usuario.get().getEmail())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .payer(payerRequest)
                    .externalReference(registrarPreCompra(produto))
                    .build();

            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao criar pagamento";
        }
    }

}
