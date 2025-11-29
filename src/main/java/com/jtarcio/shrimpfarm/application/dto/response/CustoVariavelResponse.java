package com.jtarcio.shrimpfarm.application.dto.response;

import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustoVariavelResponse {

    private Long id;
    private Long loteId;
    private String loteCodigo;
    private LocalDate dataLancamento;
    private CategoriaGastoEnum categoria;
    private String descricao;
    private BigDecimal valor;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
