package com.jtarcio.shrimpfarm.application.dto.response;

import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
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
public class LoteResponse {

    private Long id;
    private Long viveiroId;
    private String viveiroCodigo;
    private String viveiroNome;
    private Long fazendaId;
    private String fazendaNome;
    private String codigo;
    private LocalDate dataPovoamento;
    private LocalDate dataDespesca;
    private Integer quantidadePosLarvas;
    private BigDecimal custoPosLarvas;
    private BigDecimal densidadeInicial;
    private StatusLoteEnum status;
    private Integer diasCultivo;
    private String observacoes;
    private Integer quantidadeBiometrias;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
