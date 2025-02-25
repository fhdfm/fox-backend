package br.com.foxconcursos.repositories;

import br.com.foxconcursos.domain.TaxaCidade;
import br.com.foxconcursos.domain.TaxaEstado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxaEstadoRepository extends CrudRepository<TaxaEstado, String> {
    List<TaxaEstado> findAll();    List<TaxaEstado> findByEstado(String estado);


}
