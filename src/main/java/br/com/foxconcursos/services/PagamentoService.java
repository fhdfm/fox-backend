package br.com.foxconcursos.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.foxconcursos.domain.Pagamento;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.dto.MatriculaRequest;
import br.com.foxconcursos.repositories.PagamentoRepository;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    private final MatriculaService matriculaService;

    public PagamentoService(PagamentoRepository repository, MatriculaService matriculaService) {
        this.repository = repository;
        this.matriculaService = matriculaService;
    }

    public String registrarPreCompra(UUID productId) {
        Pagamento pagamento = new Pagamento();
        pagamento.setProdutoId(productId);

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
            if (payment.getProdutoId().toString().startsWith("000000"))
                matriculaRequest.setDataFim(LocalDateTime.now().plusMonths(3));
            this.matriculaService.matricular(matriculaRequest);
        }
    }
    
}
