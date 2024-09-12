package br.com.foxconcursos.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
      }
      
      @Override
      public void afterPropertiesSet() {
        setRealmName("fox-realm");
        super.afterPropertiesSet();
      }      
    
}
