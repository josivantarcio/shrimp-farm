package com.jtarcio.shrimpfarm.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiometriaRequest {

    @NotNull(message = "ID do lote é obrigatório")
    private Long loteId;

    @NotNull(message = "Data da biometria é obrigatória")
    @PastOrPresent(message = "Data da biometria não pode ser futura")
    private LocalDate dataBiometria;

    @NotNull(message = "Peso médio é obrigatório")
    @DecimalMin(value = "0.001", message = "Peso médio deve ser maior que zero")
    @Digits(integer = 8, fraction = 3, message = "Peso médio inválido")
    private BigDecimal pesoMedio;

    @NotNull(message = "Quantidade amostrada é obrigatória")
    @Min(value = 1, message = "Quantidade amostrada deve ser maior que zero")
    private Integer quantidadeAmostrada;

    @DecimalMin(value = "0.001", message = "Peso total da amostra deve ser maior que zero")
    @Digits(integer = 10, fraction = 3, message = "Peso total da amostra inválido")
    private BigDecimal pesoTotalAmostra;

    private String observacoes;
}
