package br.com.foxconcursos.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.domain.UsuarioLogado;
import br.com.foxconcursos.services.AuthenticationService;
import br.com.foxconcursos.services.impl.UsuarioServiceImpl;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UsuarioServiceImpl usuarioService;

    public AuthenticationController(
            AuthenticationService authenticationService,
            UsuarioServiceImpl usuarioService
    ) {
        this.authenticationService = authenticationService;
        this.usuarioService = usuarioService;
    }

    @PostMapping(value = "/api/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> authenticate(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.authenticationService.authenticate(authentication));
    }

    @PostMapping(value = "/api/token/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> tokenRequest) {

        String refreshToken = tokenRequest.get("refresh_token");

        try {
            Jwt jwt = this.authenticationService.decodeToken(refreshToken);
            String username = jwt.getSubject();

            // Extraindo o escopo do JWT (geralmente como uma string)
            String scope = (String) jwt.getClaims().get("scope");

            if (!"refresh_token".equals(scope)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid scope"));
            }

            UsuarioLogado usuarioLogado = usuarioService.loadUserByUsername(username);

            Authentication auth = new PreAuthenticatedAuthenticationToken(usuarioLogado, null, usuarioLogado.getAuthorities());

            Map<String, String> tokens = this.authenticationService.authenticate(auth);
            return ResponseEntity.ok(tokens);

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
