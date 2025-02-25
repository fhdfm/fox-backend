package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Endereco;
import br.com.foxconcursos.repositories.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco buscarPorId(UUID id) {
        return enderecoRepository.findById(id).orElse(null);
    }

    public  Endereco  buscarPorUsuarioId(UUID usuarioId) {
        return enderecoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }
}
