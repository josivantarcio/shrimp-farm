package com.jtarcio.shrimpfarm.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Login é obrigatório")
    private String login; // pode ser email ou username

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
