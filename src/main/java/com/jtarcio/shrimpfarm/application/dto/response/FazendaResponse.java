package com.jtarcio.shrimpfarm.application.dto.response;

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
public class FazendaResponse {

    private Long id;
    private String nome;
    private String proprietario;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private BigDecimal areaTotal;
    private BigDecimal areaUtil;
    private String telefone;
    private String email;
    private String observacoes;
    private Boolean ativa;
    private Integer quantidadeViveiros;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
