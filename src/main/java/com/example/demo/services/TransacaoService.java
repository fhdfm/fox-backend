package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.domain.StatusPagamento;
import com.example.demo.domain.Transacao;
import com.example.demo.repositories.TransacaoRepository;

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

    public boolean criarTransacao(Transacao transacao) {

        // Simula a chamada a API do ML
        StatusPagamento status = efetuarTransacao();

        transacao.setStatus(status);

        this.transacaoRepository.save(transacao);

        return status == StatusPagamento.PAGO;
    }

}
