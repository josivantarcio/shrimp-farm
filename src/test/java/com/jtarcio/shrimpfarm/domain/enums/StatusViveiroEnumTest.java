package com.jtarcio.shrimpfarm.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusViveiroEnumTest {

    @Test
    @DisplayName("Deve ter todos os valores esperados do enum")
    void deveConterTodosValoresEsperados() {
        StatusViveiroEnum[] valores = StatusViveiroEnum.values();

        assertThat(valores).hasSize(4);
        assertThat(valores).containsExactlyInAnyOrder(
                StatusViveiroEnum.DISPONIVEL,
                StatusViveiroEnum.OCUPADO,
                StatusViveiroEnum.MANUTENCAO,
                StatusViveiroEnum.INATIVO
        );
    }

    @Test
    @DisplayName("Deve retornar código correto para cada status")
    void deveRetornarCodigoCorreto() {
        assertThat(StatusViveiroEnum.DISPONIVEL.getCodigo()).isEqualTo(0);
        assertThat(StatusViveiroEnum.OCUPADO.getCodigo()).isEqualTo(1);
        assertThat(StatusViveiroEnum.MANUTENCAO.getCodigo()).isEqualTo(2);
        assertThat(StatusViveiroEnum.INATIVO.getCodigo()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada status")
    void deveRetornarDescricaoCorreta() {
        assertThat(StatusViveiroEnum.DISPONIVEL.getDescricao()).isEqualTo("Disponível");
        assertThat(StatusViveiroEnum.OCUPADO.getDescricao()).isEqualTo("Ocupado");
        assertThat(StatusViveiroEnum.MANUTENCAO.getDescricao()).isEqualTo("Manutenção");
        assertThat(StatusViveiroEnum.INATIVO.getDescricao()).isEqualTo("Inativo");
    }

    @Test
    @DisplayName("Deve retornar detalhes corretos para cada status")
    void deveRetornarDetalhesCorretos() {
        assertThat(StatusViveiroEnum.DISPONIVEL.getDetalhes()).isEqualTo("Viveiro pronto para receber novo lote");
        assertThat(StatusViveiroEnum.OCUPADO.getDetalhes()).isEqualTo("Viveiro com lote em cultivo");
        assertThat(StatusViveiroEnum.MANUTENCAO.getDetalhes()).isEqualTo("Viveiro em manutenção ou limpeza");
        assertThat(StatusViveiroEnum.INATIVO.getDetalhes()).isEqualTo("Viveiro desativado temporariamente");
    }

    @Test
    @DisplayName("Deve converter código para enum usando fromCodigo")
    void deveConverterCodigoParaEnum() {
        assertThat(StatusViveiroEnum.fromCodigo(0)).isEqualTo(StatusViveiroEnum.DISPONIVEL);
        assertThat(StatusViveiroEnum.fromCodigo(1)).isEqualTo(StatusViveiroEnum.OCUPADO);
        assertThat(StatusViveiroEnum.fromCodigo(2)).isEqualTo(StatusViveiroEnum.MANUTENCAO);
        assertThat(StatusViveiroEnum.fromCodigo(3)).isEqualTo(StatusViveiroEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        assertThatThrownBy(() -> StatusViveiroEnum.fromCodigo(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de status inválido: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção quando código for null")
    void deveLancarExcecaoQuandoCodigoNull() {
        assertThatThrownBy(() -> StatusViveiroEnum.fromCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código de status inválido: null");
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnum() {
        StatusViveiroEnum status = StatusViveiroEnum.valueOf("DISPONIVEL");

        assertThat(status).isEqualTo(StatusViveiroEnum.DISPONIVEL);
        assertThat(status.getDescricao()).isEqualTo("Disponível");
    }

    @Test
    @DisplayName("Deve verificar ordinal de cada valor")
    void deveVerificarOrdinalDeValores() {
        assertThat(StatusViveiroEnum.DISPONIVEL.ordinal()).isEqualTo(0);
        assertThat(StatusViveiroEnum.INATIVO.ordinal()).isEqualTo(3);
    }
}
