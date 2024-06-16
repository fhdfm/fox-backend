package br.com.foxconcursos.dto;

import java.util.UUID;

import br.com.foxconcursos.domain.StatusUsuario;
import br.com.foxconcursos.domain.Usuario;
import br.com.foxconcursos.util.FoxUtils;

public class UsuarioResponse {
    
    private UUID id;
    private String email;
    private String nome;
    private String cpf;
    private String perfil;
    private String telefone;
    private StatusUsuario status;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.perfil = usuario.getPerfil().name();
        this.telefone = usuario.getTelefone();
        this.status = usuario.getStatus();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return FoxUtils.formatarCpf(cpf);
    }

    public String getPerfil() {
        return perfil;
    }

    public String getTelefone() {
        return telefone;
    }

    public StatusUsuario getStatus() {
        return status;
    }
}
