package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum TipoNutrienteEnum {
    PROBIOTICO(0, "Probiótico", "Bactérias benéficas para qualidade da água"),
    VITAMINA(1, "Vitamina", "Suplemento vitamínico"),
    MINERAL(2, "Mineral", "Suplemento mineral"),
    IMUNOESTIMULANTE(3, "Imunoestimulante", "Reforço imunológico"),
    MELHORADOR_AGUA(4, "Melhorador de Água", "Produtos para tratamento da água");

    private final Integer codigo;
    private final String descricao;
    private final String detalhes;

    TipoNutrienteEnum(Integer codigo, String descricao, String detalhes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public static TipoNutrienteEnum fromCodigo(Integer codigo) {
        for (TipoNutrienteEnum tipo : TipoNutrienteEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de nutriente inválido: " + codigo);
    }
}
