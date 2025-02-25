package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.TaxaCidade;
import br.com.foxconcursos.domain.TaxaEstado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxaCidadeRepository extends CrudRepository<TaxaCidade, String> {
    List<TaxaCidade> findAll();
    Optional<TaxaCidade> findByEstadoAndCidade(String estado, String cidade);

}
