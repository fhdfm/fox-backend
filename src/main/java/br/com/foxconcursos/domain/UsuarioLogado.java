package br.com.foxconcursos.domain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioLogado implements UserDetails {

    private final Usuario usuario;

    public UsuarioLogado(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return this.usuario.getNome();
    }

    public PerfilUsuario getPerfil() {
        return this.usuario.getPerfil();
    }

    public UUID getId() {
        return this.usuario.getId();
    }

    public String getCpf() {
        return this.usuario.getCpf();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.usuario.getPerfil() == PerfilUsuario.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_ALUNO"), 
                    new SimpleGrantedAuthority("ROLE_EXTERNO"));
        if (this.usuario.getPerfil() == PerfilUsuario.ALUNO)
            return List.of(new SimpleGrantedAuthority("ROLE_ALUNO"));

        return List.of(new SimpleGrantedAuthority("ROLE_EXTERNO"));
    }

    @Override
    public String getPassword() {
        return this.usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return this.usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.usuario.getStatus() == StatusUsuario.ATIVO;
    }
}
