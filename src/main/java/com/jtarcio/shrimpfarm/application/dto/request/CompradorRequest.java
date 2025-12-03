package com.jtarcio.shrimpfarm.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompradorRequest {

    @NotBlank(message = "Nome do comprador é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    // Aceita CPF (11) ou CNPJ (14). Se quiser, depois validamos formato com @Pattern.
    @NotBlank(message = "CNPJ/CPF é obrigatório")
    @Size(min = 11, max = 14, message = "CNPJ/CPF deve ter entre 11 e 14 caracteres")
    private String cnpj;

    @Size(max = 100, message = "Contato deve ter no máximo 100 caracteres")
    private String contato;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Builder.Default
    private Boolean ativo = true;

}
