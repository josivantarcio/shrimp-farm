package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnidadeMedidaEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        UnidadeMedidaEnum[] valores = UnidadeMedidaEnum.values();

        assertThat(valores).hasSize(8);
        assertThat(valores).containsExactlyInAnyOrder(
                UnidadeMedidaEnum.KG,
                UnidadeMedidaEnum.G,
                UnidadeMedidaEnum.TON,
                UnidadeMedidaEnum.LITRO,
                UnidadeMedidaEnum.ML,
                UnidadeMedidaEnum.UNIDADE,
                UnidadeMedidaEnum.SACO,
                UnidadeMedidaEnum.HECTARE
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada unidade")
    void deveRetornarCodigoCorreto() {
        assertThat(UnidadeMedidaEnum.KG.getCodigo()).isEqualTo(0);
        assertThat(UnidadeMedidaEnum.G.getCodigo()).isEqualTo(1);
        assertThat(UnidadeMedidaEnum.TON.getCodigo()).isEqualTo(2);
        assertThat(UnidadeMedidaEnum.LITRO.getCodigo()).isEqualTo(3);
        assertThat(UnidadeMedidaEnum.ML.getCodigo()).isEqualTo(4);
        assertThat(UnidadeMedidaEnum.UNIDADE.getCodigo()).isEqualTo(5);
        assertThat(UnidadeMedidaEnum.SACO.getCodigo()).isEqualTo(6);
        assertThat(UnidadeMedidaEnum.HECTARE.getCodigo()).isEqualTo(7);
    }

    @Test
    @DisplayName("Deve retornar símbolo correto para cada unidade")
    void deveRetornarSimboloCorreto() {
        assertThat(UnidadeMedidaEnum.KG.getSimbolo()).isEqualTo("kg");
        assertThat(UnidadeMedidaEnum.G.getSimbolo()).isEqualTo("g");
        assertThat(UnidadeMedidaEnum.TON.getSimbolo()).isEqualTo("ton");
        assertThat(UnidadeMedidaEnum.LITRO.getSimbolo()).isEqualTo("L");
        assertThat(UnidadeMedidaEnum.ML.getSimbolo()).isEqualTo("mL");
        assertThat(UnidadeMedidaEnum.UNIDADE.getSimbolo()).isEqualTo("un");
        assertThat(UnidadeMedidaEnum.SACO.getSimbolo()).isEqualTo("sc");
        assertThat(UnidadeMedidaEnum.HECTARE.getSimbolo()).isEqualTo("ha");
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada unidade")
    void deveRetornarDescricaoCorreta() {
        assertThat(UnidadeMedidaEnum.KG.getDescricao()).isEqualTo("Quilograma");
        assertThat(UnidadeMedidaEnum.G.getDescricao()).isEqualTo("Grama");
        assertThat(UnidadeMedidaEnum.TON.getDescricao()).isEqualTo("Tonelada");
        assertThat(UnidadeMedidaEnum.LITRO.getDescricao()).isEqualTo("Litro");
        assertThat(UnidadeMedidaEnum.ML.getDescricao()).isEqualTo("Mililitro");
        assertThat(UnidadeMedidaEnum.UNIDADE.getDescricao()).isEqualTo("Unidade");
        assertThat(UnidadeMedidaEnum.SACO.getDescricao()).isEqualTo("Saco");
        assertThat(UnidadeMedidaEnum.HECTARE.getDescricao()).isEqualTo("Hectare");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(UnidadeMedidaEnum.fromCodigo(0)).isEqualTo(UnidadeMedidaEnum.KG);
        assertThat(UnidadeMedidaEnum.fromCodigo(1)).isEqualTo(UnidadeMedidaEnum.G);
        assertThat(UnidadeMedidaEnum.fromCodigo(2)).isEqualTo(UnidadeMedidaEnum.TON);
        assertThat(UnidadeMedidaEnum.fromCodigo(3)).isEqualTo(UnidadeMedidaEnum.LITRO);
        assertThat(UnidadeMedidaEnum.fromCodigo(4)).isEqualTo(UnidadeMedidaEnum.ML);
        assertThat(UnidadeMedidaEnum.fromCodigo(5)).isEqualTo(UnidadeMedidaEnum.UNIDADE);
        assertThat(UnidadeMedidaEnum.fromCodigo(6)).isEqualTo(UnidadeMedidaEnum.SACO);
        assertThat(UnidadeMedidaEnum.fromCodigo(7)).isEqualTo(UnidadeMedidaEnum.HECTARE);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> UnidadeMedidaEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de unidade de medida inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> UnidadeMedidaEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de unidade de medida inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        UnidadeMedidaEnum unidade = UnidadeMedidaEnum.valueOf("KG");

        assertThat(unidade).isEqualTo(UnidadeMedidaEnum.KG);
        assertThat(unidade.getSimbolo()).isEqualTo("kg");
        assertThat(unidade.getDescricao()).isEqualTo("Quilograma");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(UnidadeMedidaEnum.KG.ordinal()).isEqualTo(0);
        assertThat(UnidadeMedidaEnum.HECTARE.ordinal()).isEqualTo(7);
    }
}
