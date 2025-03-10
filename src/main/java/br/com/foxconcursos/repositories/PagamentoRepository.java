package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Pagamento;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PagamentoRepository extends CrudRepository<Pagamento, UUID> {

    boolean existsByMpId(String mpId);

    List<Pagamento> findByStatusAndMpIdIsNotNull(String status);

}
