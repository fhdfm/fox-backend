package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.Instituicao;
import br.com.foxconcursos.repositories.InstituicaoRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
    }

    public Instituicao salvar(Instituicao instituicao) {
        if (instituicao.getNome() == null || instituicao.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da instituicao.");

        UUID id = instituicao.getId();

        if (id == null) {
            if (instituicaoRepository.existsByNome(instituicao.getNome()))
                throw new IllegalArgumentException("Instituicao já cadastrada.");
            return instituicaoRepository.save(instituicao);
        }

        Instituicao instituicaoDB = instituicaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instituicao não encontrada."));

        if (!instituicaoDB.getNome().equals(instituicao.getNome())
                && instituicaoRepository.existsByNome(instituicao.getNome()))
            throw new IllegalArgumentException("Instituicao já cadastrada.");

        instituicaoDB.setNome(instituicao.getNome());

        return instituicaoRepository.save(instituicao);
    }

    public List<Instituicao> findAll(String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAll();

        Instituicao instituicao =
                FoxUtils.criarObjetoDinamico(filter, Instituicao.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Correspondência parcial
                .withIgnoreCase() // Ignorar case
                .withIgnoreNullValues(); // Ignorar valores nulos            

        Iterable<Instituicao> instituicaos =
                instituicaoRepository.findAll(
                        Example.of(instituicao, matcher));

        List<Instituicao> response =
                StreamSupport.stream(instituicaos.spliterator(), false)
                        .collect(Collectors.toList());

        return response;
    }

    public List<Instituicao> findAll() {
        return instituicaoRepository.findAll();
    }


    public void deletar(UUID instituicaoId) {
        instituicaoRepository.deleteById(instituicaoId);
    }

    public Instituicao findById(UUID instituicaoId) {
        return instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new IllegalArgumentException("Instituicao não encontrada."));
    }

}
