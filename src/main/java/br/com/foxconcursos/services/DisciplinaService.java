package br.com.foxconcursos.services;

import br.com.foxconcursos.domain.CursoDisciplina;
import br.com.foxconcursos.domain.Disciplina;
import br.com.foxconcursos.repositories.CursoDisciplinaRepository;
import br.com.foxconcursos.repositories.DisciplinaRepository;
import br.com.foxconcursos.util.FoxUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoDisciplinaRepository cursoDisciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository,
                             CursoDisciplinaRepository cursoDisciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoDisciplinaRepository = cursoDisciplinaRepository;
    }

    public Disciplina salvar(Disciplina disciplina) {

        // Verifica se o nome da disciplina foi informado
        if (disciplina.getNome() == null || disciplina.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome da disciplina.");

        // Verifica se o tipo da disciplina foi informado
        if (disciplina.getTipo() == null)
            throw new IllegalArgumentException("Informe o tipo da disciplina (CONCURSO, ENEM, OAB).");

        UUID id = disciplina.getId();

        // Se o ID da disciplina não foi informado, é um novo cadastro
        if (id == null) {
            // Verifica se já existe uma disciplina com o mesmo nome e tipo
            if (disciplinaRepository.existsByNomeAndTipo(disciplina.getNome(), disciplina.getTipo()))
                throw new IllegalArgumentException("Disciplina já cadastrada com este tipo.");

            // Salva a nova disciplina
            return disciplinaRepository.save(disciplina);
        }

        // Busca a disciplina pelo ID informado
        Disciplina disciplinaDB = disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

        // Verifica se o nome e o tipo são diferentes e se já existe outra disciplina com o mesmo nome e tipo
        if (!disciplinaDB.getNome().equals(disciplina.getNome())
                || !disciplinaDB.getTipo().equals(disciplina.getTipo())) {
            if (disciplinaRepository.existsByNomeAndTipo(disciplina.getNome(), disciplina.getTipo()))
                throw new IllegalArgumentException("Disciplina já cadastrada com este tipo.");
        }

        // Atualiza o nome e o tipo da disciplina
        disciplinaDB.setNome(disciplina.getNome());
        disciplinaDB.setTipo(disciplina.getTipo());

        // Salva a disciplina atualizada
        return disciplinaRepository.save(disciplina);
    }


    public List<Disciplina> findAll(String filter) throws Exception {

        if (filter == null || filter.isBlank())
            return this.findAll();

        Disciplina disciplina =
                FoxUtils.criarObjetoDinamico(filter, Disciplina.class);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Iterable<Disciplina> disciplinas =
                disciplinaRepository.findAll(
                        Example.of(disciplina, matcher),
                        Sort.by(Sort.Direction.ASC, "nome"));

        List<Disciplina> response =
                StreamSupport.stream(disciplinas.spliterator(), false)
                        .collect(Collectors.toList());

        return response;
    }

    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }

    public List<Disciplina> findByCursoId(UUID cursoId) {

        Map<UUID, Disciplina> disciplinas = disciplinaRepository.findAll().stream()
                .collect(Collectors.toMap(Disciplina::getId, d -> d));

        List<CursoDisciplina> disciplinaIds = cursoDisciplinaRepository.findByCursoId(cursoId);

        return disciplinaIds.stream()
                .map(d -> disciplinas.get(d.getDisciplinaId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void adicionarDisciplinas(UUID cursoId, UUID[] disciplinaIds) {

        if (cursoId == null)
            throw new IllegalArgumentException("Informe o curso.");

        if (disciplinaIds == null || disciplinaIds.length == 0)
            throw new IllegalArgumentException("Informe as disciplinas.");

        cursoDisciplinaRepository.deleteByCursoId(cursoId);

        for (UUID disciplinaId : disciplinaIds) {
            CursoDisciplina cursoDisciplina =
                    new CursoDisciplina(cursoId, disciplinaId);
            cursoDisciplinaRepository.save(cursoDisciplina);
        }
    }

    public void removerDisciplina(CursoDisciplina cursoDisciplina) {
        cursoDisciplinaRepository.deleteByCursoIdAndDisciplinaId(
                cursoDisciplina.getCursoId(), cursoDisciplina.getDisciplinaId());
    }

    public void deletar(UUID disciplinaId) {
        disciplinaRepository.deleteById(disciplinaId);
    }

    public Disciplina findById(UUID disciplinaId) {
        return disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));
    }

    public boolean existsByCursoId(UUID cursoId) {
        return cursoDisciplinaRepository.existsByCursoId(cursoId);
    }
}
