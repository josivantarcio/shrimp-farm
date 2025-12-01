package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriterioRateioEnergiaTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        CriterioRateioEnergia[] valores = CriterioRateioEnergia.values();

        assertThat(valores).hasSize(3);
        assertThat(valores).containsExactlyInAnyOrder(
                CriterioRateioEnergia.DIAS_CULTIVO,
                CriterioRateioEnergia.BIOMASSA,
                CriterioRateioEnergia.IGUALITARIO
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada critério")
    void deveRetornarCodigoCorreto() {
        assertThat(CriterioRateioEnergia.DIAS_CULTIVO.getCodigo()).isEqualTo(1);
        assertThat(CriterioRateioEnergia.BIOMASSA.getCodigo()).isEqualTo(2);
        assertThat(CriterioRateioEnergia.IGUALITARIO.getCodigo()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada critério")
    void deveRetornarDescricaoCorreta() {
        assertThat(CriterioRateioEnergia.DIAS_CULTIVO.getDescricao())
                .isEqualTo("Proporcional aos dias de cultivo");
        assertThat(CriterioRateioEnergia.BIOMASSA.getDescricao())
                .isEqualTo("Proporcional à biomassa estimada");
        assertThat(CriterioRateioEnergia.IGUALITARIO.getDescricao())
                .isEqualTo("Divisão igualitária entre lotes");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(CriterioRateioEnergia.fromCodigo(1)).isEqualTo(CriterioRateioEnergia.DIAS_CULTIVO);
        assertThat(CriterioRateioEnergia.fromCodigo(2)).isEqualTo(CriterioRateioEnergia.BIOMASSA);
        assertThat(CriterioRateioEnergia.fromCodigo(3)).isEqualTo(CriterioRateioEnergia.IGUALITARIO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> CriterioRateioEnergia.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de critério de rateio inválido: 999");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        CriterioRateioEnergia criterio = CriterioRateioEnergia.valueOf("BIOMASSA");

        assertThat(criterio).isEqualTo(CriterioRateioEnergia.BIOMASSA);
        assertThat(criterio.getDescricao()).isEqualTo("Proporcional à biomassa estimada");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(CriterioRateioEnergia.DIAS_CULTIVO.ordinal()).isEqualTo(0);
        assertThat(CriterioRateioEnergia.IGUALITARIO.ordinal()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar string formatada no toString")
    void deveRetornarStringFormatadaNoToString() {
        assertThat(CriterioRateioEnergia.DIAS_CULTIVO.toString())
                .isEqualTo("1 - Proporcional aos dias de cultivo");
        assertThat(CriterioRateioEnergia.BIOMASSA.toString())
                .isEqualTo("2 - Proporcional à biomassa estimada");
        assertThat(CriterioRateioEnergia.IGUALITARIO.toString())
                .isEqualTo("3 - Divisão igualitária entre lotes");
    }
}
