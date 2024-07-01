package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Cargo;
import br.com.foxconcursos.repositories.CargoRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public Cargo salvar(Cargo cargo) {
        if (cargo.getNome() == null || cargo.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do cargo.");

        UUID id = cargo.getId();

        if (id == null) {
            if (cargoRepository.existsByNome(cargo.getNome()))
                throw new IllegalArgumentException("Cargo já cadastrado.");
            return cargoRepository.save(cargo);
        }

        Cargo cargoDB = cargoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado."));

        if (!cargoDB.getNome().equals(cargo.getNome())
                && cargoRepository.existsByNome(cargo.getNome()))
            throw new IllegalArgumentException("Cargo já cadastrado.");

        cargoDB.setNome(cargo.getNome());

        return cargoRepository.save(cargo);
    }

    public List<Cargo> findAll(String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAll();

        Cargo cargo =
                FoxUtils.criarObjetoDinamico(filter, Cargo.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
                .withIgnoreCase() // Ignorar case
                .withIgnoreNullValues(); // Ignorar valores nulos            

        Iterable<Cargo> cargos =
                cargoRepository.findAll(
                        Example.of(cargo, matcher));

        List<Cargo> response =
                StreamSupport.stream(cargos.spliterator(), false)
                        .collect(Collectors.toList());

        return response;
    }

    public List<Cargo> findAll() {
        return cargoRepository.findAll();
    }


    public void deletar(UUID cargoId) {
        cargoRepository.deleteById(cargoId);
    }

    public Cargo findById(UUID cargoId) {
        return cargoRepository.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado."));
    }

}
