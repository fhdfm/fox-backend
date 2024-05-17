package br.com.foxconcursos.repositories;

import java.util.UUID;

import br.com.foxconcursos.domain.Banca;
import br.com.foxconcursos.repositories.custom.CustomCrudRepository;

public interface BancaRepository extends CustomCrudRepository<Banca, UUID> {

   Boolean existsByNome(String nome);

}