package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoRacaoEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        TipoRacaoEnum[] valores = TipoRacaoEnum.values();

        assertThat(valores).hasSize(4);
        assertThat(valores).containsExactlyInAnyOrder(
                TipoRacaoEnum.INICIAL,
                TipoRacaoEnum.CRESCIMENTO,
                TipoRacaoEnum.ENGORDA,
                TipoRacaoEnum.FINALIZACAO
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada tipo")
    void deveRetornarCodigoCorreto() {
        assertThat(TipoRacaoEnum.INICIAL.getCodigo()).isEqualTo(0);
        assertThat(TipoRacaoEnum.CRESCIMENTO.getCodigo()).isEqualTo(1);
        assertThat(TipoRacaoEnum.ENGORDA.getCodigo()).isEqualTo(2);
        assertThat(TipoRacaoEnum.FINALIZACAO.getCodigo()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada tipo")
    void deveRetornarDescricaoCorreta() {
        assertThat(TipoRacaoEnum.INICIAL.getDescricao()).isEqualTo("Inicial");
        assertThat(TipoRacaoEnum.CRESCIMENTO.getDescricao()).isEqualTo("Crescimento");
        assertThat(TipoRacaoEnum.ENGORDA.getDescricao()).isEqualTo("Engorda");
        assertThat(TipoRacaoEnum.FINALIZACAO.getDescricao()).isEqualTo("Finalização");
    }

    @Test
    @DisplayName("Deve retornar detalhes corretos para cada tipo")
    void deveRetornarDetalhesCorretos() {
        assertThat(TipoRacaoEnum.INICIAL.getDetalhes()).isEqualTo("Ração para fase inicial (PL até 30 dias)");
        assertThat(TipoRacaoEnum.CRESCIMENTO.getDetalhes()).isEqualTo("Ração para fase de crescimento (30-60 dias)");
        assertThat(TipoRacaoEnum.ENGORDA.getDetalhes()).isEqualTo("Ração para fase de engorda (60+ dias)");
        assertThat(TipoRacaoEnum.FINALIZACAO.getDetalhes()).isEqualTo("Ração para finalização pré-despesca");
    }

    @Test
    @DisplayName("Deve retornar percentual de proteína correto para cada tipo")
    void deveRetornarProteinaCorreta() {
        assertThat(TipoRacaoEnum.INICIAL.getProteina()).isEqualTo(45.0);
        assertThat(TipoRacaoEnum.CRESCIMENTO.getProteina()).isEqualTo(38.0);
        assertThat(TipoRacaoEnum.ENGORDA.getProteina()).isEqualTo(35.0);
        assertThat(TipoRacaoEnum.FINALIZACAO.getProteina()).isEqualTo(32.0);
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(TipoRacaoEnum.fromCodigo(0)).isEqualTo(TipoRacaoEnum.INICIAL);
        assertThat(TipoRacaoEnum.fromCodigo(1)).isEqualTo(TipoRacaoEnum.CRESCIMENTO);
        assertThat(TipoRacaoEnum.fromCodigo(2)).isEqualTo(TipoRacaoEnum.ENGORDA);
        assertThat(TipoRacaoEnum.fromCodigo(3)).isEqualTo(TipoRacaoEnum.FINALIZACAO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> TipoRacaoEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de tipo de ração inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> TipoRacaoEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de tipo de ração inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        TipoRacaoEnum tipo = TipoRacaoEnum.valueOf("INICIAL");

        assertThat(tipo).isEqualTo(TipoRacaoEnum.INICIAL);
        assertThat(tipo.getDescricao()).isEqualTo("Inicial");
        assertThat(tipo.getProteina()).isEqualTo(45.0);
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(TipoRacaoEnum.INICIAL.ordinal()).isEqualTo(0);
        assertThat(TipoRacaoEnum.FINALIZACAO.ordinal()).isEqualTo(3);
    }
}
