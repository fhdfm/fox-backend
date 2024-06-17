package br.com.foxconcursos.services;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.UsuarioLogado;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        
        this.decoder = jwtDecoder;
        this.encoder = jwtEncoder;

    }

    public String generatePasswordToken(String email) {
        Instant now = Instant.now();
        
        var claims = JwtClaimsSet.builder()
            .issuer("portal-fox")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(21600))
            .subject(email)
            .claim("scope", "password_reset")
            .build();

        return this.encoder.encode
                (JwtEncoderParameters.from(claims))
                    .getTokenValue();
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
                
        String scopes = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        UsuarioLogado usuario = (UsuarioLogado) authentication.getPrincipal();

        var claims = JwtClaimsSet.builder()
            .issuer("portal-fox")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject(authentication.getName())
            .claim("scope", scopes)
            .claim("nome", usuario.getNome())
            .claim("id", usuario.getId())
            .claim("cpf", usuario.getCpf())
            .build();

        return this.encoder.encode
                (JwtEncoderParameters.from(claims))
                    .getTokenValue();
    }

    @SuppressWarnings("all")
    public boolean validarToken(String token) {
        try {
            Jwt jwt = this.decoder.decode(token);
            Instant expiration = jwt.getExpiresAt();
            return expiration.isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }
}
