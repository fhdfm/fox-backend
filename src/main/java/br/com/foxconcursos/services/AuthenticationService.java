package br.com.foxconcursos.services;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;

    public AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Map<String, String> authenticate(Authentication authentication) {
        return this.jwtService.generateToken(authentication);
    }

    public Jwt decodeToken(String token) {
        return this.jwtService.decodeToken(token);
    }

}
