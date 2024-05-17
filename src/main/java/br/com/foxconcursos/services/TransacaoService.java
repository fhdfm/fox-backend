package br.com.foxconcursos.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.StatusPagamento;
import br.com.foxconcursos.domain.Transacao;
import br.com.foxconcursos.repositories.TransacaoRepository;

@Service
public class TransacaoService {
    
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    // Mock
    private StatusPagamento efetuarTransacao() {
        return StatusPagamento.PAGO;
    }

    public Transacao criarTransacao(Transacao transacao) {

        // Simula a chamada a API do ML
        StatusPagamento status = efetuarTransacao();

        transacao.setStatus(status);
        transacao.setTransactionId(UUID.randomUUID().toString());

        return this.transacaoRepository.save(transacao);
    }

}
