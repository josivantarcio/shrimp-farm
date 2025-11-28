package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum TipoRacaoEnum {
    INICIAL(0, "Inicial", "Ração para fase inicial (PL até 30 dias)", 45.0),
    CRESCIMENTO(1, "Crescimento", "Ração para fase de crescimento (30-60 dias)", 38.0),
    ENGORDA(2, "Engorda", "Ração para fase de engorda (60+ dias)", 35.0),
    FINALIZACAO(3, "Finalização", "Ração para finalização pré-despesca", 32.0);

    private final Integer codigo;
    private final String descricao;
    private final String detalhes;
    private final Double proteina; // % de proteína

    TipoRacaoEnum(Integer codigo, String descricao, String detalhes, Double proteina) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhes = detalhes;
        this.proteina = proteina;
    }

    public static TipoRacaoEnum fromCodigo(Integer codigo) {
        for (TipoRacaoEnum tipo : TipoRacaoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de ração inválido: " + codigo);
    }
}
