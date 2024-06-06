package br.com.foxconcursos.services;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UsuarioServiceImpl usuarioService;

    public AuthenticationService(JwtService jwtService, 
        UsuarioServiceImpl usuarioService) {
        
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;

    }

    public String authenticate(Authentication authentication) {
        return this.jwtService.generateToken(authentication);
    }

    public UUID obterUsuarioLogado() {
        
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        UsuarioLogado usuario =
            this.usuarioService.loadUserByUsername(
                authentication.getName());
        
        return usuario.getId();
    }
}
