package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import br.com.foxconcursos.domain.Transacao;

public interface TransacaoRepository extends ListCrudRepository<Transacao, UUID> {
}
