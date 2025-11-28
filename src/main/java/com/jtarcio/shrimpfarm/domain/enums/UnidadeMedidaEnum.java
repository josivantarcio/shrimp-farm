package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum UnidadeMedidaEnum {
    KG(0, "kg", "Quilograma"),
    G(1, "g", "Grama"),
    TON(2, "ton", "Tonelada"),
    LITRO(3, "L", "Litro"),
    ML(4, "mL", "Mililitro"),
    UNIDADE(5, "un", "Unidade"),
    SACO(6, "sc", "Saco"),
    HECTARE(7, "ha", "Hectare");

    private final Integer codigo;
    private final String simbolo;
    private final String descricao;

    UnidadeMedidaEnum(Integer codigo, String simbolo, String descricao) {
        this.codigo = codigo;
        this.simbolo = simbolo;
        this.descricao = descricao;
    }

    public static UnidadeMedidaEnum fromCodigo(Integer codigo) {
        for (UnidadeMedidaEnum unidade : UnidadeMedidaEnum.values()) {
            if (unidade.getCodigo().equals(codigo)) {
                return unidade;
            }
        }
        throw new IllegalArgumentException("Código de unidade de medida inválido: " + codigo);
    }
}
