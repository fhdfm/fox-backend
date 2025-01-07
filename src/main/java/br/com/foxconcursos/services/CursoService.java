package br.com.foxconcursos.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.foxconcursos.domain.Banca;
import br.com.foxconcursos.domain.Curso;
import br.com.foxconcursos.domain.Simulado;
import br.com.foxconcursos.domain.Status;
import br.com.foxconcursos.dto.CursoRequest;
import br.com.foxconcursos.dto.CursoResponse;
import br.com.foxconcursos.dto.ProdutoCursoResponse;
import br.com.foxconcursos.dto.StorageInput;
import br.com.foxconcursos.dto.StorageOutput;
import br.com.foxconcursos.repositories.CursoRepository;
import br.com.foxconcursos.repositories.SimuladoRepository;
import br.com.foxconcursos.util.FoxUtils;
import br.com.foxconcursos.util.SecurityUtil;

@Service
public class CursoService {
    
    private final CursoRepository cursoRepository;
    private final BancaService bancaService;
    private final DisciplinaService disciplinaService;
    private final SimuladoRepository simuladoRepository;

    private final StorageService storageService;

    public CursoService(CursoRepository cursoRepository, BancaService bancaService, 
        DisciplinaService disciplinaService, SimuladoRepository simuladoRepository, 
        StorageService storageService) {
        this.cursoRepository = cursoRepository;
        this.bancaService = bancaService;
        this.disciplinaService = disciplinaService;
        this.simuladoRepository = simuladoRepository;
        this.storageService = storageService;
    }

    public ProdutoCursoResponse getMatriculaCursoResponse(UUID id) {
        
        Curso curso = this.cursoRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Curso não encontrado."));

        Banca banca = this.bancaService.findById(curso.getBancaId());
        
        return new ProdutoCursoResponse(curso, banca.getNome());
    }

    public UUID create(CursoRequest request) throws Exception {

        validarEntrada(request, true);

        Curso curso = new Curso(request);

        MultipartFile file = request.getImagem();

        StorageInput input = new StorageInput.Builder()
                .withFileInputStream(file.getInputStream())
                .withFileName(file.getOriginalFilename())
                .withFileSize(file.getSize())
                .withMimeType(file.getContentType())
                .isPublic(true)
                .build();

        StorageOutput output = this.storageService.upload(input);
        curso.setImagem(output.getUrl());

        this.cursoRepository.save(curso);

        return curso.getId();
    }

    public void update(UUID id, CursoRequest request) throws Exception {

        Curso curso = this.cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalAccessError("Curso não encontrado."));

        validarEntrada(request, false);

        if (request.hasUpload()) {

            MultipartFile file = request.getImagem();

            StorageInput input = new StorageInput.Builder()
                    .withFileInputStream(file.getInputStream())
                    .withMimeType(file.getContentType())
                    .withFileName(file.getOriginalFilename())
                    .withFileSize(file.getSize())
                    .isPublic(true)
                    .build();

            StorageOutput output = this.storageService.upload(input);
            curso.setImagem(output.getUrl());
        }

        this.cursoRepository.save(curso);

    }

    private void validarEntrada(CursoRequest request, boolean validarImagem) {
        if (request.getTitulo() == null) {
            throw new IllegalArgumentException("Título é requerido.");
        }

        if (cursoRepository.existsByTitulo(request.getTitulo())) {
            throw new IllegalArgumentException("Já existe um curso com este título.");
        }

        if (request.getDescricao() == null) {
            throw new IllegalArgumentException("Descrição é requerida.");
        }

        if (validarImagem) {
            if (request.getImagem() == null) {
                throw new IllegalArgumentException("A imagem é obrigatória.");
            }
        }

        if (request.getValor() == null) {
            throw new IllegalArgumentException("Preço é requerido.");
        }

        // TODO - add demais campos obrigatorios.
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

    public CursoResponse findById(UUID id) {
        
        if (id == null) {
            throw new IllegalArgumentException("Id é requerido.");
        }

        Curso curso = this.cursoRepository.findById(id).orElse(null);

        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }

        Banca banca = this.bancaService.findById(curso.getBancaId());

        CursoResponse result = new CursoResponse(curso);
        result.setPossuiDisciplinas(disciplinaService.existsByCursoId(id));
        result.setBanca(banca.getNome());

        return result;
    }

    public List<Simulado> findSimuladosNaoMatriculadosByCursoId(UUID cursoId){
        return simuladoRepository.findSimuladosNaoMatriculadosByCursoId(cursoId, SecurityUtil.obterUsuarioLogado().getId() );
    }

    public List<Simulado> findSimuladosByCursoIdAndUsuarioId(UUID cursoId){
        return simuladoRepository.findSimuladosByCursoIdAndUsuarioId(cursoId, SecurityUtil.obterUsuarioLogado().getId() );
    }
    public List<Simulado> findSimuladosByCursoId(UUID cursoId){
        return simuladoRepository.findByCursoId(cursoId);
    }

    public Page<CursoResponse> findAll(Pageable pageable, String filter) throws Exception {

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
        
        Page<CursoResponse> result = cursos.map(new Function<Curso, CursoResponse>() {
            public CursoResponse apply(Curso curso) {
                String banca = bancas.get(curso.getBancaId());
                CursoResponse response = new CursoResponse(curso);
                response.setBanca(banca);
                response.setPossuiDisciplinas(disciplinaService.existsByCursoId(curso.getId()));
                return response;
            }
        });

        return result;
    }

    //gitpublic CursoAlunoResponse iniciar(UUID cursoId)

}
