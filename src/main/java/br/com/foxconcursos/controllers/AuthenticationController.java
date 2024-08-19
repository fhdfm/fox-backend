package br.com.foxconcursos.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.foxconcursos.services.AuthenticationService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationService authenticationService, 
        AuthenticationManager authenticationManager) {
        
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
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

            // Convertendo o escopo em authorities
            List<SimpleGrantedAuthority> authorities = Arrays.stream(scope.split(" "))
                .map(role -> new SimpleGrantedAuthority("SCOPE_" + role)) // Prefixando com "SCOPE_"
                .collect(Collectors.toList());

            Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, null, authorities)
            );

            Map<String, String> tokens = this.authenticationService.authenticate(auth);
            return ResponseEntity.ok(tokens);

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
