package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.LoginRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import com.jtarcio.shrimpfarm.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioResponse login(LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getLogin());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),   // pode ser email ou username
                        request.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Usuario usuario = (Usuario) authentication.getPrincipal();

        log.info("Login realizado com sucesso para usuário id: {}", usuario.getId());

        // Monta uma resposta básica com dados do usuário + token
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .papel(usuario.getPapel())
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                // se quiser, depois criamos um DTO próprio de AuthResponse com campo token
                .build();
    }
}
