package com.jtarcio.shrimpfarm.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FazendaRequest {

    @NotBlank(message = "Nome da fazenda é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 100, message = "Nome do proprietário deve ter no máximo 100 caracteres")
    private String proprietario;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    private String estado;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String cep;

    @DecimalMin(value = "0.0", inclusive = false, message = "Área total deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Área total inválida")
    private BigDecimal areaTotal;

    @DecimalMin(value = "0.0", inclusive = false, message = "Área útil deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Área útil inválida")
    private BigDecimal areaUtil;

    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Telefone inválido")
    private String telefone;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    private String observacoes;

    @Builder.Default
    private Boolean ativa = true;
}
