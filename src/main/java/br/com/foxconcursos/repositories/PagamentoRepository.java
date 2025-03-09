package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.Pagamento;
import br.com.foxconcursos.domain.TipoProduto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PagamentoRepository extends CrudRepository<Pagamento, UUID> {

    boolean existsByMpId(String mpId);

    List<Pagamento> findByStatusAndMpIdIsNotNullAndTipoNot(String status, TipoProduto tipo);

}
