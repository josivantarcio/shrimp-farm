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
public class DespescaRequest {

    @NotNull(message = "ID do lote é obrigatório")
    private Long loteId;

    private Long compradorId;

    @NotNull(message = "Data de despesca é obrigatória")
    @PastOrPresent(message = "Data de despesca não pode ser futura")
    private LocalDate dataDespesca;

    @NotNull(message = "Peso total é obrigatório")
    @DecimalMin(value = "0.001", message = "Peso total deve ser maior que zero")
    @Digits(integer = 12, fraction = 2, message = "Peso total inválido")
    private BigDecimal pesoTotal;

    @NotNull(message = "Quantidade despescada é obrigatória")
    @Min(value = 1, message = "Quantidade despescada deve ser maior que zero")
    private Integer quantidadeDespescada;

    @NotNull(message = "Peso médio final é obrigatório")
    @DecimalMin(value = "0.001", message = "Peso médio final deve ser maior que zero")
    @Digits(integer = 8, fraction = 3, message = "Peso médio final inválido")
    private BigDecimal pesoMedioFinal;

    @DecimalMin(value = "0.0", message = "Preço de venda deve ser positivo")
    @Digits(integer = 10, fraction = 2, message = "Preço de venda inválido")
    private BigDecimal precoVendaKg;

    @DecimalMin(value = "0.0", message = "Custo de despesca deve ser positivo")
    @Digits(integer = 10, fraction = 2, message = "Custo de despesca inválido")
    private BigDecimal custoDespesca;

    private String observacoes;
}
