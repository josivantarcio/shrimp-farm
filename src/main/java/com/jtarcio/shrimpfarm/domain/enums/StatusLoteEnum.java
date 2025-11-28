package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum StatusLoteEnum {
    PLANEJADO(0, "Planejado", "Lote planejado mas ainda não povoado"),
    ATIVO(1, "Ativo", "Lote em cultivo ativo"),
    FINALIZADO(2, "Finalizado", "Lote despescado e finalizado"),
    CANCELADO(3, "Cancelado", "Lote cancelado antes da despesca");

    private final Integer codigo;
    private final String descricao;
    private final String detalhes;

    StatusLoteEnum(Integer codigo, String descricao, String detalhes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public static StatusLoteEnum fromCodigo(Integer codigo) {
        for (StatusLoteEnum status : StatusLoteEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status inválido: " + codigo);
    }
}
