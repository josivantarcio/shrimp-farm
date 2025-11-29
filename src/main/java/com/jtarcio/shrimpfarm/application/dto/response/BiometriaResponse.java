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
public class BiometriaResponse {

    private Long id;
    private Long loteId;
    private String loteCodigo;
    private LocalDate dataBiometria;
    private Integer diaCultivo;
    private BigDecimal pesoMedio;
    private Integer quantidadeAmostrada;
    private BigDecimal pesoTotalAmostra;
    private BigDecimal ganhoPesoDiario;
    private BigDecimal biomassaEstimada;
    private BigDecimal sobrevivenciaEstimada;
    private BigDecimal fatorConversaoAlimentar;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
