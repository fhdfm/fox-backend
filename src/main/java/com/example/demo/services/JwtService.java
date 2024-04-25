package com.example.demo.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.demo.domain.UsuarioLogado;
import com.example.demo.dto.MatriculaAtivaResponse;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final MatriculaService matriculaService;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, 
        MatriculaService matriculaService) {
        this.encoder = jwtEncoder;
        this.matriculaService = matriculaService;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
                
        String scopes = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        UsuarioLogado usuario = (UsuarioLogado) authentication.getPrincipal();

        List<MatriculaAtivaResponse> matriculas = this.matriculaService
            .getMatriculasAtivas(usuario.getId());

        var claims = JwtClaimsSet.builder()
            .issuer("portal-fox")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject(authentication.getName())
            .claim("scope", scopes)
            .claim("nome", usuario.getNome())
            .claim("matriculas", matriculas)
            .build();

        return this.encoder.encode
                (JwtEncoderParameters.from(claims))
                    .getTokenValue();
    }
}
