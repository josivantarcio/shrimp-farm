package com.jtarcio.shrimpfarm.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardKPIsResponse {

    private Integer totalLotesAtivos;
    private Integer totalViveirosOcupados;
    private BigDecimal diasMediosCultivo;
    private BigDecimal pesoMedioAtual;
    private BigDecimal biomassaTotalAtual;
    private BigDecimal custoMedioPorKg;
    private BigDecimal lucroMedioPorKg;
    private BigDecimal taxaSobrevivenciaMedia;
    private BigDecimal fcaMedia;
}
