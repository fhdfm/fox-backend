package br.com.foxconcursos.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private final FoxBasicAuthenticationEntryPoint entryPoint;

    public SecurityConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey, FoxBasicAuthenticationEntryPoint entryPoint) {
        
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.entryPoint = entryPoint;

    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .exceptionHandling(exception -> exception
    //             .authenticationEntryPoint(new FoxAuthenticationEntryPoint())) // Configura o AuthenticationEntryPoint
    //         .authorizeHttpRequests(auth ->
    //             auth.requestMatchers("/api/admin/**").hasAuthority("SCOPE_ROLE_ADMIN")
    //                 .requestMatchers("/api/aluno/**").hasAnyAuthority("SCOPE_ROLE_EXTERNO", "SCOPE_ROLE_ALUNO", "SCOPE_ROLE_ADMIN")
    //                 .anyRequest().permitAll())
    //         .httpBasic(Customizer.withDefaults())
    //         .oauth2ResourceServer(conf -> conf.jwt(Customizer.withDefaults()));

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Desabilita o CSRF, comum em APIs REST sem estado
            .csrf(csrf -> csrf.disable())

            // Configura o gerenciamento de sessão sem estado
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configura as permissões e autorizações por rota
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_ROLE_ADMIN")  // Apenas ROLE_ADMIN
                .requestMatchers("/api/aluno/**").hasAnyAuthority("SCOPE_ROLE_EXTERNO", "SCOPE_ROLE_ALUNO", "SCOPE_ROLE_ADMIN")  // Diferentes níveis de acesso
                .anyRequest().permitAll())  // Permite todas as outras requisições
                
            // Configura autenticação HTTP Basic (se necessário)
            .httpBasic(basic ->
                basic.authenticationEntryPoint(entryPoint)
            )
            
            // Configura o servidor OAuth2 para usar JWTs
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults()));  // JWT é usado para autenticação via OAuth2

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
