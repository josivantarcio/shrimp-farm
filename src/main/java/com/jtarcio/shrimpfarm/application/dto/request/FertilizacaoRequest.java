package com.jtarcio.shrimpfarm.application.dto.request;

import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
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
public class FertilizacaoRequest {

    @NotNull(message = "ID do lote é obrigatório")
    private Long loteId;

    private Long fornecedorId;

    @NotNull(message = "Data de aplicação é obrigatória")
    @PastOrPresent(message = "Data de aplicação não pode ser futura")
    private LocalDate dataAplicacao;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 100, message = "Nome do produto deve ter no máximo 100 caracteres")
    private String produto;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
    @Digits(integer = 10, fraction = 3, message = "Quantidade inválida")
    private BigDecimal quantidade;

    @NotNull(message = "Unidade é obrigatória")
    private UnidadeMedidaEnum unidade;

    @DecimalMin(value = "0.0", inclusive = false, message = "Custo unitário deve ser positivo")
    @Digits(integer = 10, fraction = 2, message = "Custo unitário inválido")
    private BigDecimal custoUnitario;

    @Size(max = 50, message = "Finalidade deve ter no máximo 50 caracteres")
    private String finalidade;

    private String observacoes;
}
