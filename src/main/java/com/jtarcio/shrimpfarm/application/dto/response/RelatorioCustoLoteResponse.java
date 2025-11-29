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
public class RelatorioCustoLoteResponse {

    private Long loteId;
    private String loteCodigo;
    private String viveiroNome;
    private Integer diasCultivo;

    // Custos detalhados
    private BigDecimal custoRacao;
    private BigDecimal custoNutrientes;
    private BigDecimal custoFertilizacao;
    private BigDecimal custosVariaveis;
    private BigDecimal custoTotal;

    // Biomassa e produção
    private BigDecimal biomassaAtual;
    private BigDecimal pesoMedioAtual;
    private Integer quantidadeEstimada;

    // Indicadores
    private BigDecimal custoPorKg;
    private BigDecimal fca;
    private BigDecimal taxaSobrevivencia;

    // Projeções (se disponível)
    private BigDecimal receitaProjetada;
    private BigDecimal lucroProjetado;
    private BigDecimal margemProjetada;
}
