package br.com.foxconcursos.config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FoxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if (exception instanceof DisabledException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usuário está inativo");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciais inválidas");
        }
    }
      

}
