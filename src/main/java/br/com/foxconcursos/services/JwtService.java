package br.com.foxconcursos.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import br.com.foxconcursos.domain.UsuarioLogado;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder decoder) {
        this.encoder = jwtEncoder;
        this.decoder = decoder;
    }

    public String generatePasswordToken(String email, Instant now, long expiresAt) {
                
        var claims = JwtClaimsSet.builder()
            .issuer("portal-fox")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresAt))
            .subject(email)
            .claim("scope", "password_reset")
            .build();

        return this.encoder.encode
                (JwtEncoderParameters.from(claims))
                    .getTokenValue();
    }

    public Map<String, String> generateToken(Authentication authentication) {
        
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

        String accesToken = this.encoder.encode(
            JwtEncoderParameters.from(claims)).getTokenValue();

        var refreshTokenClaims = JwtClaimsSet.builder()
            .issuer("portal-fox")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(2592000))
            .subject(authentication.getName())
            .claim("scope", "refresh_token")
            .build();
        
        String refreshToken = this.encoder.encode(
            JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accesToken);
        tokens.put("refresh_token", refreshToken);

        return tokens;
    }

    public Jwt decodeToken(String token) {
        try {
            return decoder.decode(token);
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido.", e);
        }
    }

}
