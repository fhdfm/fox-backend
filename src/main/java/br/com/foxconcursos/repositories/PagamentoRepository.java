package br.com.foxconcursos.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.foxconcursos.domain.Pagamento;

public interface PagamentoRepository extends CrudRepository<Pagamento, UUID> {
}
