package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Pagamento;
import br.com.foxconcursos.domain.TipoProduto;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.exception.UsuarioNaoEncontradoException;
import br.com.foxconcursos.repositories.PagamentoRepository;
import br.com.foxconcursos.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
public class MatriculaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MatriculaScheduler.class);

    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MercadoPagoService mercadoPagoService;
    private final EmailService emailService;
    private final MatriculaService matriculaService;

    public MatriculaScheduler(
            PagamentoRepository pagamentoRepository,
            MercadoPagoService mercadoPagoService,
            UsuarioRepository usuarioRepository,
            EmailService emailService,
            MatriculaService matriculaService) {
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.mercadoPagoService = mercadoPagoService;
        this.matriculaService = matriculaService;
        this.emailService = emailService;
    }

    // O cron "0 0/5 * * * ?" executa a cada 5 minutos
    @Scheduled(cron = "0 0/5 * * * ?")
    public void executarTarefa() {
        List<Pagamento> listaCompras = pagamentoRepository
                .findByStatusAndMpIdIsNotNullAndTipoNot("pending", TipoProduto.APOSTILA);

        if (listaCompras.isEmpty()) {
            logger.info("Nenhum pagamento pendente encontrado para processar.");
            return;
        }

        for (Pagamento pagamento : listaCompras) {
            try {
                processarPagamento(pagamento);
            } catch (Exception e) {
                logger.error("Erro ao processar matrícula para pagamento {}. Realizando rollback apenas deste registro. Motivo: {}",
                        pagamento.getMpId(), e.getMessage(), e);
                revertPagamento(pagamento);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processarPagamento(Pagamento pagamento) {
        Map<String, Object> payment = mercadoPagoService.findByPaymentId(pagamento.getMpId());
        String status = (String) payment.get("status");

        if ("approved".equals(status)) {
            logger.info("Pagamento {} aprovado. Iniciando processamento de matrícula.", pagamento.getMpId());
            pagamento.setData(
                    OffsetDateTime.parse("" + payment.get("date_last_updated"))
                            .toLocalDateTime()
            );
            pagamento.setValor(new BigDecimal("" + payment.get("transaction_amount")));
            pagamento.setStatus("approved");

            pagamentoRepository.save(pagamento);

            MatriculaRequest matriculaRequest = new MatriculaRequest();
            matriculaRequest.setUsuarioId(pagamento.getUsuarioId());
            matriculaRequest.setProdutoId(pagamento.getProdutoId());
            matriculaRequest.setValor(pagamento.getValor());

            if (pagamento.getTipo() == TipoProduto.QUESTOES) {
                matriculaRequest.setDataFim(LocalDateTime.now().plusMonths(pagamento.getPeriodo()));
            }

            matriculaService.matricular(matriculaRequest);
            Usuario usuario = usuarioRepository.findById(pagamento.getUsuarioId())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

            emailService.enviarEmailPagamentoAprovado(usuario.getEmail(), usuario.getNome(), pagamento.getTitulo(), pagamento.getTipo(), null, pagamento.getMpId());

            logger.info("Matrícula realizada com sucesso para pagamento {}.", pagamento.getMpId());
        } else {
            logger.info("Pagamento {} não aprovado. Status atual: {}", pagamento.getMpId(), status);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revertPagamento(Pagamento pagamento) {
        pagamento.setStatus("pending");
        pagamentoRepository.save(pagamento);
        logger.info("Pagamento {} revertido para status 'pending'.", pagamento.getMpId());
    }
}
