package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.Questao;
import br.com.foxconcursos.dto.BancoQuestaoResponse;
import br.com.foxconcursos.dto.QuestaoRequest;
import br.com.foxconcursos.dto.QuestaoResponse;
import br.com.foxconcursos.services.QuestaoService;
import br.com.foxconcursos.util.FoxUtils;

@RestController
@RequestMapping(path = "/api/admin/questoes", 
    consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestaoController {
    
    private final QuestaoService questaoService;

    public QuestaoController(QuestaoService questaoService) {
        this.questaoService = questaoService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody QuestaoRequest request) {
        
        UUID id = this.questaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@RequestBody QuestaoRequest request, 
        @PathVariable UUID id) {
        
        this.questaoService.update(request, id);
        return ResponseEntity.status(HttpStatus.OK).body("Questao: " + id 
            + " atualizada com sucesso.");

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {

        this.questaoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Questao: " 
            + id + " deletada com sucesso.");

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BancoQuestaoResponse> findAll(@RequestParam(required = false) String filter, 
        @RequestParam(required = true, defaultValue = "25") Integer limit, 
        @RequestParam(required = true, defaultValue = "0") Integer offset) throws Exception {

        Questao questao = new Questao();;
        
        if (filter != null) {
            questao = FoxUtils.criarObjetoDinamico(filter, Questao.class);
        }

        int quantidadeRegistros = this.questaoService.getRecordCount(questao);
        int quantidadeDePaginas = quantidadeRegistros / limit;

        BancoQuestaoResponse response = new BancoQuestaoResponse();
        response.setTotalDeRegistros(quantidadeRegistros);
        response.setTotalDePaginas(quantidadeDePaginas);

        List<QuestaoResponse> questoes = 
            this.questaoService.findAll(questao, limit, offset);

        response.setQuestoes(questoes);

        Map<String, String> filtro = this.questaoService.getFiltroCorrente(questao);

        response.setFiltros(filtro);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<QuestaoResponse> findById(@PathVariable UUID id) {
        QuestaoResponse questao = this.questaoService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(questao);

    }

}
