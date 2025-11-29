package com.jtarcio.shrimpfarm.domain.enums;

public enum CriterioRateioEnergia {

    DIAS_CULTIVO(1, "Proporcional aos dias de cultivo"),
    BIOMASSA(2, "Proporcional à biomassa estimada"),
    IGUALITARIO(3, "Divisão igualitária entre lotes");

    private final int codigo;
    private final String descricao;

    CriterioRateioEnergia(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Busca o enum pelo código numérico
     */
    public static CriterioRateioEnergia fromCodigo(int codigo) {
        for (CriterioRateioEnergia criterio : CriterioRateioEnergia.values()) {
            if (criterio.getCodigo() == codigo) {
                return criterio;
            }
        }
        throw new IllegalArgumentException("Código de critério de rateio inválido: " + codigo);
    }

    @Override
    public String toString() {
        return String.format("%d - %s", codigo, descricao);
    }
}
