package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum StatusViveiroEnum {
    DISPONIVEL(0, "Disponível", "Viveiro pronto para receber novo lote"),
    OCUPADO(1, "Ocupado", "Viveiro com lote em cultivo"),
    MANUTENCAO(2, "Manutenção", "Viveiro em manutenção ou limpeza"),
    INATIVO(3, "Inativo", "Viveiro desativado temporariamente");

    private final Integer codigo;
    private final String descricao;
    private final String detalhes;

    StatusViveiroEnum(Integer codigo, String descricao, String detalhes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public static StatusViveiroEnum fromCodigo(Integer codigo) {
        for (StatusViveiroEnum status : StatusViveiroEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status inválido: " + codigo);
    }
}
