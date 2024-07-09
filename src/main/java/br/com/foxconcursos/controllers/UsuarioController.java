package br.com.foxconcursos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.dto.AlterarPasswordRequest;
import br.com.foxconcursos.dto.ProdutoResponse;
import br.com.foxconcursos.dto.UsuarioResponse;
import br.com.foxconcursos.services.ProdutoService;
import br.com.foxconcursos.services.RecuperarPasswordService;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {

    private final UsuarioServiceImpl service;
    private final ProdutoService produtoService;
    private final RecuperarPasswordService recuperarPasswordService;


    public UsuarioController(UsuarioServiceImpl service, 
        ProdutoService produtoService, RecuperarPasswordService recuperarPasswordService) {


        this.service = service;
        this.produtoService = produtoService;
        this.recuperarPasswordService = recuperarPasswordService;

    }
    
    @PostMapping(value = "/api/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> create(@RequestBody Usuario user) {
        
        Usuario savedUser = this.service.save(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser.getId());
    }

    @DeleteMapping(value = "/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> delete(@PathVariable UUID id) {
        this.service.desativar(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PostMapping(value = "/api/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        
        if (email == null || email.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Email não pode ser vazio.");

        Usuario user = this.service.findByEmail(email);

        return ResponseEntity.status(HttpStatus.OK)
            .body(this.recuperarPasswordService.recuperarPassword(user));
    }

    @PostMapping(value = "/api/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resetPassword(@RequestBody AlterarPasswordRequest request) {
        
        if (request.getToken() == null || request.getToken().isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token não pode ser vazio.");

        if (request.getNovaSenha() == null || request.getNovaSenha().isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Senha não pode ser vazia.");

        return ResponseEntity.status(HttpStatus.OK)
            .body(this.service.alterarPassowrd(request.getToken(), request.getNovaSenha()));
    }

    @PutMapping(value = "/api/admin/usuarios/{id}", 
        consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> update(@PathVariable UUID id, @RequestBody Usuario user) {
        user.setId(id);
        this.service.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @GetMapping(value = "/api/admin/usuarios/{id}/produtos-nao-matriculados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProdutoResponse>> obterProdutosNaoMatriculados(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.obterProdutosNaoMatriculados(id));
    }

    @GetMapping(value = "/api/alunos/usuarios/{id}/produtos-matriculados")
    @PreAuthorize("hasRole('USUARIO') or hasRole('EXTERNO')")
    public ResponseEntity<List<ProdutoResponse>> obterProdutosMatriculados(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.obterProdutosMatriculados(id));
    }    

    @GetMapping(value = "/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioResponse>> findAll(Pageable pageable,
        @RequestParam(required = false) String filter) throws Exception {
        
        return ResponseEntity.ok(service.findAll(pageable, filter));
    }

    @GetMapping(value = "/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

}
