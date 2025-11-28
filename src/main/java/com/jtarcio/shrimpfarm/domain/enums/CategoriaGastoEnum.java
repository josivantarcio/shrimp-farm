package com.jtarcio.shrimpfarm.domain.enums;

import lombok.Getter;

@Getter
public enum CategoriaGastoEnum {
    RACAO(0, "Ração", "Custos com ração"),
    NUTRIENTE(1, "Nutriente", "Custos com probióticos e suplementos"),
    FERTILIZACAO(2, "Fertilização", "Custos com fertilizantes"),
    POS_LARVA(3, "Pós-Larva", "Custo de aquisição de pós-larvas"),
    ENERGIA(4, "Energia", "Custos com energia elétrica"),
    COMBUSTIVEL(5, "Combustível", "Custos com diesel/gasolina"),
    MAO_OBRA(6, "Mão de Obra", "Custos com pessoal"),
    MANUTENCAO(7, "Manutenção", "Manutenção de equipamentos e estruturas"),
    OUTROS(8, "Outros", "Outros custos variáveis");

    private final Integer codigo;
    private final String descricao;
    private final String detalhes;

    CategoriaGastoEnum(Integer codigo, String descricao, String detalhes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public static CategoriaGastoEnum fromCodigo(Integer codigo) {
        for (CategoriaGastoEnum categoria : CategoriaGastoEnum.values()) {
            if (categoria.getCodigo().equals(codigo)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Código de categoria de gasto inválido: " + codigo);
    }
}
