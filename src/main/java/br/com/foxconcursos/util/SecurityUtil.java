package br.com.foxconcursos.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.foxconcursos.domain.UsuarioLogado;

public class SecurityUtil {
    

    public static UsuarioLogado obterUsuarioLogado() {
        
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        
        return (UsuarioLogado) authentication.getPrincipal();
    }
}
