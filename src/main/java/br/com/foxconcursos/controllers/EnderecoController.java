package br.com.foxconcursos.controllers;

import br.com.foxconcursos.domain.Endereco;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.services.EnderecoService;
import br.com.foxconcursos.util.SecurityUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( value = "/api/endereco",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> buscarPorId(@PathVariable UUID id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        return endereco != null ? ResponseEntity.ok(endereco) : ResponseEntity.notFound().build();
    }

    @GetMapping()
    public Endereco buscarPorUsuarioId() {
        UsuarioLogado currentUser = SecurityUtil.obterUsuarioLogado();
        return enderecoService.buscarPorUsuarioId(currentUser.getId());
    }

    @PostMapping
    public Endereco salvarOuAtualizar(@RequestBody Endereco endereco) {
        return enderecoService.salvar(endereco);
    }
}
