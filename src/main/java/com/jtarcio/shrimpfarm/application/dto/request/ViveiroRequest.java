package com.jtarcio.shrimpfarm.application.dto.request;

import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
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
public class ViveiroRequest {

    @NotNull(message = "ID da fazenda é obrigatório")
    private Long fazendaId;

    @NotBlank(message = "Código do viveiro é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String codigo;

    @NotBlank(message = "Nome do viveiro é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @DecimalMin(value = "0.0", inclusive = false, message = "Área deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Área inválida")
    private BigDecimal area;

    @DecimalMin(value = "0.0", inclusive = false, message = "Profundidade deve ser maior que zero")
    @Digits(integer = 5, fraction = 2, message = "Profundidade inválida")
    private BigDecimal profundidadeMedia;

    @DecimalMin(value = "0.0", inclusive = false, message = "Volume deve ser maior que zero")
    @Digits(integer = 12, fraction = 2, message = "Volume inválido")
    private BigDecimal volume;

    @Builder.Default
    private StatusViveiroEnum status = StatusViveiroEnum.DISPONIVEL;

    private String observacoes;

    @Builder.Default
    private Boolean ativo = true;
}
