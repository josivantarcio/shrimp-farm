package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha()) // Será criptografada no service
                .papel(request.getPapel())  // ← MUDANÇA: papel
                .ativo(request.getAtivo())
                .build();
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .papel(usuario.getPapel())  // ← MUDANÇA: getPapel()
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .build();
    }

    public void updateEntity(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setPapel(request.getPapel());  // ← MUDANÇA: setPapel()
        usuario.setAtivo(request.getAtivo());
        // Senha atualizada separadamente no service
    }
}
