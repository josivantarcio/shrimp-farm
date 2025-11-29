package com.jtarcio.shrimpfarm.application.dto.response;

import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViveiroResponse {

    private Long id;
    private Long fazendaId;
    private String fazendaNome;
    private String codigo;
    private String nome;
    private BigDecimal area;
    private BigDecimal profundidadeMedia;
    private BigDecimal volume;
    private StatusViveiroEnum status;
    private String observacoes;
    private Boolean ativo;
    private Integer quantidadeLotes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
