package com.jtarcio.shrimpfarm.application.dto.response;

import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
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
public class RacaoResponse {

    private Long id;
    private Long loteId;
    private String loteCodigo;
    private Long fornecedorId;
    private String fornecedorNome;
    private LocalDate dataAplicacao;
    private TipoRacaoEnum tipoRacao;
    private String marca;
    private BigDecimal quantidade;
    private UnidadeMedidaEnum unidade;
    private BigDecimal custoUnitario;
    private BigDecimal custoTotal;
    private BigDecimal proteinaPercentual;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
