package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .username(request.getUsername())
                .email(request.getEmail())
                .senha(request.getSenha()) // será criptografada no service
                .papel(request.getPapel())
                .ativo(request.getAtivo())
                .build();
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .papel(usuario.getPapel())
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .build();
    }

    public List<UsuarioResponse> toResponseList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.getNome());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPapel(request.getPapel());
        usuario.setAtivo(request.getAtivo());
        // senha é tratada separadamente no service
    }
}
