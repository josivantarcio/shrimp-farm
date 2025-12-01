package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoriaGastoEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        CategoriaGastoEnum[] valores = CategoriaGastoEnum.values();

        assertThat(valores).hasSize(9);
        assertThat(valores).containsExactlyInAnyOrder(
                CategoriaGastoEnum.RACAO,
                CategoriaGastoEnum.NUTRIENTE,
                CategoriaGastoEnum.FERTILIZACAO,
                CategoriaGastoEnum.POS_LARVA,
                CategoriaGastoEnum.ENERGIA,
                CategoriaGastoEnum.COMBUSTIVEL,
                CategoriaGastoEnum.MAO_OBRA,
                CategoriaGastoEnum.MANUTENCAO,
                CategoriaGastoEnum.OUTROS
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada categoria")
    void deveRetornarCodigoCorreto() {
        assertThat(CategoriaGastoEnum.RACAO.getCodigo()).isEqualTo(0);
        assertThat(CategoriaGastoEnum.NUTRIENTE.getCodigo()).isEqualTo(1);
        assertThat(CategoriaGastoEnum.FERTILIZACAO.getCodigo()).isEqualTo(2);
        assertThat(CategoriaGastoEnum.POS_LARVA.getCodigo()).isEqualTo(3);
        assertThat(CategoriaGastoEnum.ENERGIA.getCodigo()).isEqualTo(4);
        assertThat(CategoriaGastoEnum.COMBUSTIVEL.getCodigo()).isEqualTo(5);
        assertThat(CategoriaGastoEnum.MAO_OBRA.getCodigo()).isEqualTo(6);
        assertThat(CategoriaGastoEnum.MANUTENCAO.getCodigo()).isEqualTo(7);
        assertThat(CategoriaGastoEnum.OUTROS.getCodigo()).isEqualTo(8);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada categoria")
    void deveRetornarDescricaoCorreta() {
        assertThat(CategoriaGastoEnum.RACAO.getDescricao()).isEqualTo("Ração");
        assertThat(CategoriaGastoEnum.NUTRIENTE.getDescricao()).isEqualTo("Nutriente");
        assertThat(CategoriaGastoEnum.FERTILIZACAO.getDescricao()).isEqualTo("Fertilização");
        assertThat(CategoriaGastoEnum.POS_LARVA.getDescricao()).isEqualTo("Pós-Larva");
        assertThat(CategoriaGastoEnum.ENERGIA.getDescricao()).isEqualTo("Energia");
        assertThat(CategoriaGastoEnum.COMBUSTIVEL.getDescricao()).isEqualTo("Combustível");
        assertThat(CategoriaGastoEnum.MAO_OBRA.getDescricao()).isEqualTo("Mão de Obra");
        assertThat(CategoriaGastoEnum.MANUTENCAO.getDescricao()).isEqualTo("Manutenção");
        assertThat(CategoriaGastoEnum.OUTROS.getDescricao()).isEqualTo("Outros");
    }

    @Test
    @DisplayName("Deve retornar detalhes corretos para cada categoria")
    void deveRetornarDetalhesCorretos() {
        assertThat(CategoriaGastoEnum.RACAO.getDetalhes()).isEqualTo("Custos com ração");
        assertThat(CategoriaGastoEnum.NUTRIENTE.getDetalhes()).isEqualTo("Custos com probióticos e suplementos");
        assertThat(CategoriaGastoEnum.FERTILIZACAO.getDetalhes()).isEqualTo("Custos com fertilizantes");
        assertThat(CategoriaGastoEnum.POS_LARVA.getDetalhes()).isEqualTo("Custo de aquisição de pós-larvas");
        assertThat(CategoriaGastoEnum.ENERGIA.getDetalhes()).isEqualTo("Custos com energia elétrica");
        assertThat(CategoriaGastoEnum.COMBUSTIVEL.getDetalhes()).isEqualTo("Custos com diesel/gasolina");
        assertThat(CategoriaGastoEnum.MAO_OBRA.getDetalhes()).isEqualTo("Custos com pessoal");
        assertThat(CategoriaGastoEnum.MANUTENCAO.getDetalhes()).isEqualTo("Manutenção de equipamentos e estruturas");
        assertThat(CategoriaGastoEnum.OUTROS.getDetalhes()).isEqualTo("Outros custos variáveis");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(CategoriaGastoEnum.fromCodigo(0)).isEqualTo(CategoriaGastoEnum.RACAO);
        assertThat(CategoriaGastoEnum.fromCodigo(1)).isEqualTo(CategoriaGastoEnum.NUTRIENTE);
        assertThat(CategoriaGastoEnum.fromCodigo(2)).isEqualTo(CategoriaGastoEnum.FERTILIZACAO);
        assertThat(CategoriaGastoEnum.fromCodigo(3)).isEqualTo(CategoriaGastoEnum.POS_LARVA);
        assertThat(CategoriaGastoEnum.fromCodigo(4)).isEqualTo(CategoriaGastoEnum.ENERGIA);
        assertThat(CategoriaGastoEnum.fromCodigo(5)).isEqualTo(CategoriaGastoEnum.COMBUSTIVEL);
        assertThat(CategoriaGastoEnum.fromCodigo(6)).isEqualTo(CategoriaGastoEnum.MAO_OBRA);
        assertThat(CategoriaGastoEnum.fromCodigo(7)).isEqualTo(CategoriaGastoEnum.MANUTENCAO);
        assertThat(CategoriaGastoEnum.fromCodigo(8)).isEqualTo(CategoriaGastoEnum.OUTROS);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> CategoriaGastoEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de categoria de gasto inválido: 999");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        CategoriaGastoEnum categoria = CategoriaGastoEnum.valueOf("RACAO");

        assertThat(categoria).isEqualTo(CategoriaGastoEnum.RACAO);
        assertThat(categoria.getDescricao()).isEqualTo("Ração");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(CategoriaGastoEnum.RACAO.ordinal()).isEqualTo(0);
        assertThat(CategoriaGastoEnum.OUTROS.ordinal()).isEqualTo(8);
    }
}
