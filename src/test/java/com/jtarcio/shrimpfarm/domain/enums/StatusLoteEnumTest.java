package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusLoteEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        StatusLoteEnum[] valores = StatusLoteEnum.values();

        assertThat(valores).hasSize(4);
        assertThat(valores).containsExactlyInAnyOrder(
                StatusLoteEnum.PLANEJADO,
                StatusLoteEnum.ATIVO,
                StatusLoteEnum.FINALIZADO,
                StatusLoteEnum.CANCELADO
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada status")
    void deveRetornarCodigoCorreto() {
        assertThat(StatusLoteEnum.PLANEJADO.getCodigo()).isEqualTo(0);
        assertThat(StatusLoteEnum.ATIVO.getCodigo()).isEqualTo(1);
        assertThat(StatusLoteEnum.FINALIZADO.getCodigo()).isEqualTo(2);
        assertThat(StatusLoteEnum.CANCELADO.getCodigo()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada status")
    void deveRetornarDescricaoCorreta() {
        assertThat(StatusLoteEnum.PLANEJADO.getDescricao()).isEqualTo("Planejado");
        assertThat(StatusLoteEnum.ATIVO.getDescricao()).isEqualTo("Ativo");
        assertThat(StatusLoteEnum.FINALIZADO.getDescricao()).isEqualTo("Finalizado");
        assertThat(StatusLoteEnum.CANCELADO.getDescricao()).isEqualTo("Cancelado");
    }

    @Test
    @DisplayName("Deve retornar detalhes corretos para cada status")
    void deveRetornarDetalhesCorretos() {
        assertThat(StatusLoteEnum.PLANEJADO.getDetalhes()).isEqualTo("Lote planejado mas ainda não povoado");
        assertThat(StatusLoteEnum.ATIVO.getDetalhes()).isEqualTo("Lote em cultivo ativo");
        assertThat(StatusLoteEnum.FINALIZADO.getDetalhes()).isEqualTo("Lote despescado e finalizado");
        assertThat(StatusLoteEnum.CANCELADO.getDetalhes()).isEqualTo("Lote cancelado antes da despesca");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(StatusLoteEnum.fromCodigo(0)).isEqualTo(StatusLoteEnum.PLANEJADO);
        assertThat(StatusLoteEnum.fromCodigo(1)).isEqualTo(StatusLoteEnum.ATIVO);
        assertThat(StatusLoteEnum.fromCodigo(2)).isEqualTo(StatusLoteEnum.FINALIZADO);
        assertThat(StatusLoteEnum.fromCodigo(3)).isEqualTo(StatusLoteEnum.CANCELADO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> StatusLoteEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de status inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> StatusLoteEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de status inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        StatusLoteEnum status = StatusLoteEnum.valueOf("ATIVO");

        assertThat(status).isEqualTo(StatusLoteEnum.ATIVO);
        assertThat(status.getDescricao()).isEqualTo("Ativo");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(StatusLoteEnum.PLANEJADO.ordinal()).isEqualTo(0);
        assertThat(StatusLoteEnum.CANCELADO.ordinal()).isEqualTo(3);
    }
}
