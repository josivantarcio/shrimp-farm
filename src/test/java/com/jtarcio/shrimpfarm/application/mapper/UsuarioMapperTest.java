package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    private UsuarioRequest criarRequest() {
        return UsuarioRequest.builder()
                .nome("João da Silva")
                .email("joao.silva@email.com")
                .senha("senha123")
                .papel(RoleEnum.GERENTE)
                .build();
    }

    private Usuario criarEntity() {
        return Usuario.builder()
                .id(100L)
                .nome("João da Silva")
                .email("joao.silva@email.com")
                .senha("$2a$10$hashedPassword")
                .papel(RoleEnum.GERENTE)
                .ativo(true)
                .dataCriacao(LocalDateTime.of(2025, 3, 10, 10, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 11, 11, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter UsuarioRequest para Usuario")
    void deveConverterRequestParaEntity() {
        UsuarioRequest request = criarRequest();

        Usuario entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getEmail()).isEqualTo(request.getEmail());
        assertThat(entity.getSenha()).isEqualTo(request.getSenha());
        assertThat(entity.getNome()).isEqualTo(request.getNome());
    }

    @Test
    @DisplayName("Deve atualizar Usuario existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Usuario entity = criarEntity();
        String senhaOriginal = entity.getSenha(); // Guarda senha original

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Maria Santos")
                .email("maria.santos@email.com")
                .senha("novaSenha456")
                .papel(RoleEnum.ADMIN)
                .build();

        mapper.updateEntity(entity, request);

        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getEmail()).isEqualTo(request.getEmail());
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        // Senha não é atualizada pelo mapper (precisa ser hasheada antes)
        assertThat(entity.getSenha()).isEqualTo(senhaOriginal);
    }

    @Test
    @DisplayName("Deve converter Usuario para UsuarioResponse")
    void deveConverterEntityParaResponse() {
        Usuario entity = criarEntity();

        UsuarioResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getNome()).isEqualTo(entity.getNome());
        assertThat(response.getEmail()).isEqualTo(entity.getEmail());
        assertThat(response.getPapel()).isEqualTo(entity.getPapel());
        assertThat(response.getAtivo()).isEqualTo(entity.getAtivo());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
