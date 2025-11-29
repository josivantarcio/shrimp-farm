package com.jtarcio.shrimpfarm.application.dto.request;

import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
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
public class LoteRequest {

    @NotNull(message = "ID do viveiro é obrigatório")
    private Long viveiroId;

    @NotBlank(message = "Código do lote é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String codigo;

    @NotNull(message = "Data de povoamento é obrigatória")
    @PastOrPresent(message = "Data de povoamento não pode ser futura")
    private LocalDate dataPovoamento;

    @PastOrPresent(message = "Data de despesca não pode ser futura")
    private LocalDate dataDespesca;

    @NotNull(message = "Quantidade de pós-larvas é obrigatória")
    @Min(value = 1, message = "Quantidade de pós-larvas deve ser maior que zero")
    private Integer quantidadePosLarvas;

    @DecimalMin(value = "0.0", inclusive = false, message = "Custo de pós-larvas deve ser maior que zero")
    @Digits(integer = 12, fraction = 2, message = "Custo de pós-larvas inválido")
    private BigDecimal custoPosLarvas;

    @DecimalMin(value = "0.0", inclusive = false, message = "Densidade inicial deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Densidade inicial inválida")
    private BigDecimal densidadeInicial;

    @Builder.Default
    private StatusLoteEnum status = StatusLoteEnum.PLANEJADO;

    private String observacoes;
}
