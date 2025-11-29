package com.jtarcio.shrimpfarm.application.dto.response;

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
public class DespescaResponse {

    private Long id;
    private Long loteId;
    private String loteCodigo;
    private Long compradorId;
    private String compradorNome;
    private LocalDate dataDespesca;
    private BigDecimal pesoTotal;
    private Integer quantidadeDespescada;
    private BigDecimal pesoMedioFinal;
    private BigDecimal taxaSobrevivencia;
    private BigDecimal precoVendaKg;
    private BigDecimal receitaTotal;
    private BigDecimal custoDespesca;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
