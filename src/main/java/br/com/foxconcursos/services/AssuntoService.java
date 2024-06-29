package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Assunto;
import br.com.foxconcursos.repositories.AssuntoRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AssuntoService {

    private final AssuntoRepository assuntoRepository;

    public AssuntoService(AssuntoRepository assuntoRepository) {
        this.assuntoRepository = assuntoRepository;
    }

    public Assunto salvar(Assunto assunto) {
        if (assunto.getNome() == null || assunto.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do assunto.");

        if (assunto.getDisciplinaId() == null)
            throw new IllegalArgumentException("Informe a disciplina.");

        UUID id = assunto.getId();

        if (id == null) {
            if (assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), assunto.getDisciplinaId()))
                throw new IllegalArgumentException("Assunto já cadastrado.");
            return assuntoRepository.save(assunto);
        }

        Assunto assuntoDB = assuntoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assunto não encontrado."));

        if (!assuntoDB.getNome().equals(assunto.getNome())
                && assuntoRepository.existsByNomeAndDisciplinaId(assunto.getNome(), assunto.getDisciplinaId()))
            throw new IllegalArgumentException("Assunto já cadastrado.");

        assuntoDB.setNome(assunto.getNome());
        assuntoDB.setDisciplinaId(assunto.getDisciplinaId());

        return assuntoRepository.save(assunto);
    }

    public List<Assunto> findAll(String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAll();

        Assunto assunto =
                FoxUtils.criarObjetoDinamico(filter, Assunto.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
                .withIgnoreCase() // Ignorar case
                .withIgnoreNullValues(); // Ignorar valores nulos            

        Iterable<Assunto> assuntos =
                assuntoRepository.findAll(
                        Example.of(assunto, matcher));

        List<Assunto> response =
                StreamSupport.stream(assuntos.spliterator(), false)
                        .collect(Collectors.toList());

        return response;
    }

    public List<Assunto> findAll() {
        return assuntoRepository.findAll();
    }


    public void deletar(UUID assuntoId) {
        assuntoRepository.deleteById(assuntoId);
    }

    public Assunto findById(UUID assuntoId) {
        return assuntoRepository.findById(assuntoId)
                .orElseThrow(() -> new IllegalArgumentException("Assunto não encontrado."));
    }

}
