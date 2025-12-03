package com.jtarcio.shrimpfarm.application.dto.request;

import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
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
public class CustoVariavelRequest {

    @NotNull(message = "ID do lote é obrigatório")
    private Long loteId;

    @NotNull(message = "Data de lançamento é obrigatória")
    @PastOrPresent(message = "Data de lançamento não pode ser futura")
    private LocalDate dataLancamento;

    @NotNull(message = "Categoria é obrigatória")
    private CategoriaGastoEnum categoria;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser positivo")
    @Digits(integer = 12, fraction = 2, message = "Valor inválido")
    private BigDecimal valor;

    private String observacoes;
}
