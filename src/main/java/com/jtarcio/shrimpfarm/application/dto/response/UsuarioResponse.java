package com.jtarcio.shrimpfarm.application.dto.response;

import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;  // ← MUDANÇA
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String username;
    private String email;
    private RoleEnum papel;  // ← MUDANÇA: papel -> role
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
