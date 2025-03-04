package br.com.foxconcursos.config;

import br.com.foxconcursos.exception.UsuarioNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class FoxBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         AuthenticationException auth) throws IOException {

        Throwable ex = auth.getCause();

        if (ex instanceof DisabledException) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String msg = ((DisabledException) ex).getMessage();
            PrintWriter writer = res.getWriter();
            writer.println(msg);
        }

        if (ex == null && auth instanceof BadCredentialsException) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String msg = auth.getMessage();
            PrintWriter writer = res.getWriter();
            writer.println(msg);
        }

        if (ex instanceof UsuarioNaoEncontradoException) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String msg = "Usuário não existe, faça seu cadastro!";
            PrintWriter writer = res.getWriter();
            writer.println(new String(msg.getBytes(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("fox-realm");
        super.afterPropertiesSet();
    }

}
