package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoNutrienteEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        TipoNutrienteEnum[] valores = TipoNutrienteEnum.values();

        assertThat(valores).hasSize(5);
        assertThat(valores).containsExactlyInAnyOrder(
                TipoNutrienteEnum.PROBIOTICO,
                TipoNutrienteEnum.VITAMINA,
                TipoNutrienteEnum.MINERAL,
                TipoNutrienteEnum.IMUNOESTIMULANTE,
                TipoNutrienteEnum.MELHORADOR_AGUA
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada tipo")
    void deveRetornarCodigoCorreto() {
        assertThat(TipoNutrienteEnum.PROBIOTICO.getCodigo()).isEqualTo(0);
        assertThat(TipoNutrienteEnum.VITAMINA.getCodigo()).isEqualTo(1);
        assertThat(TipoNutrienteEnum.MINERAL.getCodigo()).isEqualTo(2);
        assertThat(TipoNutrienteEnum.IMUNOESTIMULANTE.getCodigo()).isEqualTo(3);
        assertThat(TipoNutrienteEnum.MELHORADOR_AGUA.getCodigo()).isEqualTo(4);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada tipo")
    void deveRetornarDescricaoCorreta() {
        assertThat(TipoNutrienteEnum.PROBIOTICO.getDescricao()).isEqualTo("Probiótico");
        assertThat(TipoNutrienteEnum.VITAMINA.getDescricao()).isEqualTo("Vitamina");
        assertThat(TipoNutrienteEnum.MINERAL.getDescricao()).isEqualTo("Mineral");
        assertThat(TipoNutrienteEnum.IMUNOESTIMULANTE.getDescricao()).isEqualTo("Imunoestimulante");
        assertThat(TipoNutrienteEnum.MELHORADOR_AGUA.getDescricao()).isEqualTo("Melhorador de Água");
    }

    @Test
    @DisplayName("Deve retornar detalhes corretos para cada tipo")
    void deveRetornarDetalhesCorretos() {
        assertThat(TipoNutrienteEnum.PROBIOTICO.getDetalhes()).isEqualTo("Bactérias benéficas para qualidade da água");
        assertThat(TipoNutrienteEnum.VITAMINA.getDetalhes()).isEqualTo("Suplemento vitamínico");
        assertThat(TipoNutrienteEnum.MINERAL.getDetalhes()).isEqualTo("Suplemento mineral");
        assertThat(TipoNutrienteEnum.IMUNOESTIMULANTE.getDetalhes()).isEqualTo("Reforço imunológico");
        assertThat(TipoNutrienteEnum.MELHORADOR_AGUA.getDetalhes()).isEqualTo("Produtos para tratamento da água");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(TipoNutrienteEnum.fromCodigo(0)).isEqualTo(TipoNutrienteEnum.PROBIOTICO);
        assertThat(TipoNutrienteEnum.fromCodigo(1)).isEqualTo(TipoNutrienteEnum.VITAMINA);
        assertThat(TipoNutrienteEnum.fromCodigo(2)).isEqualTo(TipoNutrienteEnum.MINERAL);
        assertThat(TipoNutrienteEnum.fromCodigo(3)).isEqualTo(TipoNutrienteEnum.IMUNOESTIMULANTE);
        assertThat(TipoNutrienteEnum.fromCodigo(4)).isEqualTo(TipoNutrienteEnum.MELHORADOR_AGUA);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> TipoNutrienteEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de tipo de nutriente inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> TipoNutrienteEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de tipo de nutriente inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        TipoNutrienteEnum tipo = TipoNutrienteEnum.valueOf("PROBIOTICO");

        assertThat(tipo).isEqualTo(TipoNutrienteEnum.PROBIOTICO);
        assertThat(tipo.getDescricao()).isEqualTo("Probiótico");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(TipoNutrienteEnum.PROBIOTICO.ordinal()).isEqualTo(0);
        assertThat(TipoNutrienteEnum.MELHORADOR_AGUA.ordinal()).isEqualTo(4);
    }
}
