package br.com.foxconcursos.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.Banca;
import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.dto.CursoDTO;
import br.com.foxconcursos.dto.ProdutoCursoResponse;
import br.com.foxconcursos.repositories.CursoRepository;
import br.com.foxconcursos.repositories.SimuladoRepository;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class CursoService {
    
    // private final StorageService storageService;

    private final CursoRepository cursoRepository;
    private final BancaService bancaService;
    private final DisciplinaService disciplinaService;
    private final SimuladoRepository simuladoRepository;

    public CursoService(CursoRepository cursoRepository, BancaService bancaService, 
        DisciplinaService disciplinaService,
                        SimuladoRepository simuladoRepository) {
        this.cursoRepository = cursoRepository;
        this.bancaService = bancaService;
        this.disciplinaService = disciplinaService;
        this.simuladoRepository = simuladoRepository;
    }

    public ProdutoCursoResponse getMatriculaCursoResponse(UUID id) {
        
        Curso curso = this.cursoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Curso não encontrado."));

        Banca banca = this.bancaService.findById(curso.getBancaId());
        
        return new ProdutoCursoResponse(curso, banca.getNome());
    }

    public UUID save(CursoDTO cursoDTO) {

        validarEntrada(cursoDTO);

        return this.cursoRepository.save(new Curso(cursoDTO)).getId();
    }

    private void validarEntrada(CursoDTO cursoDTO) {
        if (cursoDTO.getTitulo() == null) {
            throw new IllegalArgumentException("Título é requerido.");
        }

        if (cursoRepository.existsByTitulo(cursoDTO.getTitulo())) {
            throw new IllegalArgumentException("Já existe um curso com este título.");
        }

        if (cursoDTO.getDescricao() == null) {
            throw new IllegalArgumentException("Descrição é requerida.");
        }

        if (cursoDTO.getValor() == null) {
            throw new IllegalArgumentException("Preço é requerido.");
        }
    }

    public void delete(UUID id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        if (!this.cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        this.cursoRepository.deleteById(id);
    }

    public Curso obterPorId(UUID id) {
        return this.cursoRepository.findById(id).orElse(null);
    }

    public CursoDTO findById(UUID id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        Curso curso = this.cursoRepository.findById(id).orElse(null);

        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        Banca banca = this.bancaService.findById(curso.getBancaId());

        CursoDTO result = new CursoDTO(curso);
        result.setPossuiDisciplinas(disciplinaService.existsByCursoId(id));
        result.setNomeBanca(banca.getNome());

        return result;
    }

    public List<Simulado> findSimuladoByCursoId(UUID cursoId){
        return simuladoRepository.findSimuladoNaoMatriculadosByCursoId(cursoId, SecurityUtil.obterUsuarioLogado().getId() );
    }

    public Page<CursoDTO> findAll(Pageable pageable, String filter) throws Exception {

        Map<UUID, String> bancas = this.bancaService.findAllAsMap();

        Page<Curso> cursos = null;

        if (filter != null) {
            Curso curso = FoxUtils.criarObjetoDinamico(filter, Curso.class);
            curso.setStatus(Status.ATIVO);

            ExampleMatcher matcher = ExampleMatcher.matching()
                // .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact()) // Correspondência igual
                // .withMatcher("perfil", ExampleMatcher.GenericPropertyMatchers.exact()) // Correspondência parcial
                // .withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                // .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                // .withMatcher("cpf", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                // .withMatcher("telefone", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnoreCase() // Ignorar case
                .withIgnoreNullValues(); // Ignorar valores nulos            

            Example<Curso> example = Example.of(curso, matcher);
            cursos = this.cursoRepository.findAll(example, pageable);            
        } else {
            cursos = this.cursoRepository.findAllByStatus(pageable, Status.ATIVO);
        }
        
        Page<CursoDTO> result = cursos.map(new Function<Curso, CursoDTO>() {
            public CursoDTO apply(Curso curso) {
                String banca = bancas.get(curso.getBancaId());
                CursoDTO cursoDTO = new CursoDTO(curso);
                cursoDTO.setNomeBanca(banca);
                cursoDTO.setPossuiDisciplinas(disciplinaService.existsByCursoId(curso.getId()));
                return cursoDTO;
            }
        });

        return result;
    }

    public Map<UUID, CursoDTO> findAllAsMap() {

        Map<UUID, CursoDTO> result = new HashMap<UUID, CursoDTO>();

        Map<UUID, String> bancas = this.bancaService.findAllAsMap();

        List<Curso> cursos = this.cursoRepository.findAllByStatus(Status.ATIVO);
        for (Curso curso : cursos) {
            String banca = bancas.get(curso.getBancaId());
            CursoDTO cursoDTO = new CursoDTO(curso);
            cursoDTO.setNomeBanca(banca);
            result.put(curso.getId(), cursoDTO);
        }

        return result;
    }

}
