package br.com.foxconcursos.util;

import br.com.foxconcursos.config.SpringContext;
import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UsuarioLogado obterUsuarioLogado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UsuarioServiceImpl service = SpringContext.getBean(UsuarioServiceImpl.class);

        return service.loadUserByUsername(authentication.getName());
    }
}
