package com.example.demo.services;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsuarioResponse;
import com.example.demo.services.impl.UsuarioServiceImpl;
import com.example.demo.util.FoxUtils;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UsuarioServiceImpl usuarioService;

    public AuthenticationService(JwtService jwtService, UsuarioServiceImpl usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    public String authenticate(Authentication authentication) {
        return this.jwtService.generateToken(authentication);
    }

    public void validarRequisicao(UUID usuarioId) {
        
        if (FoxUtils.isNullOrEmpty(usuarioId))
            throw new IllegalArgumentException("Usuário não informado");

        UsuarioResponse usuario = this.usuarioService.findById(usuarioId);

        if (!usuario.getEmail().equals(getUsuarioLogado()))
            throw new IllegalArgumentException("Usuário não autorizado!");
     }

     private String getUsuarioLogado() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
     }

}
