package com.jtarcio.shrimpfarm.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DinheiroTest {

    @Test
    @DisplayName("Deve criar dinheiro com construtor padrão")
    void deveCriarDinheiroComConstrutorPadrao() {
        Dinheiro dinheiro = new Dinheiro();

        assertThat(dinheiro.getValor()).isNull();
        assertThat(dinheiro.getMoeda()).isEqualTo("BRL");
    }

    @Test
    @DisplayName("Deve criar dinheiro com BigDecimal")
    void deveCriarDinheiroComBigDecimal() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.50"));

        assertThat(dinheiro.getValor()).isEqualByComparingTo("100.50");
        assertThat(dinheiro.getMoeda()).isEqualTo("BRL");
    }

    @Test
    @DisplayName("Deve criar dinheiro com double")
    void deveCriarDinheiroComDouble() {
        Dinheiro dinheiro = new Dinheiro(100.50);

        assertThat(dinheiro.getValor()).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("Deve arredondar valor para 2 casas decimais")
    void deveArredondarValorPara2CasasDecimais() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.456"));

        assertThat(dinheiro.getValor()).isEqualByComparingTo("100.46");
    }

    @Test
    @DisplayName("Deve criar dinheiro zero quando valor for null")
    void deveCriarDinheiroZeroQuandoValorNull() {
        Dinheiro dinheiro = new Dinheiro((BigDecimal) null);

        assertThat(dinheiro.getValor()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve somar dois valores da mesma moeda")
    void deveSomarDoisValoresMesmaMoeda() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("100.00"));
        Dinheiro d2 = new Dinheiro(new BigDecimal("50.50"));

        Dinheiro resultado = d1.somar(d2);

        assertThat(resultado.getValor()).isEqualByComparingTo("150.50");
    }

    @Test
    @DisplayName("Deve lançar exceção ao somar moedas diferentes")
    void deveLancarExcecaoAoSomarMoedasDiferentes() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("100.00"), "BRL");
        Dinheiro d2 = new Dinheiro(new BigDecimal("50.00"), "USD");

        assertThatThrownBy(() -> d1.somar(d2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não é possível somar valores de moedas diferentes");
    }

    @Test
    @DisplayName("Deve subtrair dois valores da mesma moeda")
    void deveSubtrairDoisValoresMesmaMoeda() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("100.00"));
        Dinheiro d2 = new Dinheiro(new BigDecimal("30.50"));

        Dinheiro resultado = d1.subtrair(d2);

        assertThat(resultado.getValor()).isEqualByComparingTo("69.50");
    }

    @Test
    @DisplayName("Deve lançar exceção ao subtrair moedas diferentes")
    void deveLancarExcecaoAoSubtrairMoedasDiferentes() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("100.00"), "BRL");
        Dinheiro d2 = new Dinheiro(new BigDecimal("50.00"), "USD");

        assertThatThrownBy(() -> d1.subtrair(d2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não é possível subtrair valores de moedas diferentes");
    }

    @Test
    @DisplayName("Deve multiplicar valor por fator")
    void deveMultiplicarValorPorFator() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.00"));

        Dinheiro resultado = dinheiro.multiplicar(new BigDecimal("2.5"));

        assertThat(resultado.getValor()).isEqualByComparingTo("250.00");
    }

    @Test
    @DisplayName("Deve dividir valor por divisor")
    void deveDividirValorPorDivisor() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.00"));

        Dinheiro resultado = dinheiro.dividir(new BigDecimal("4"));

        assertThat(resultado.getValor()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("Deve arredondar resultado da divisão para 2 casas")
    void deveArredondarResultadoDaDivisao() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.00"));

        Dinheiro resultado = dinheiro.dividir(new BigDecimal("3"));

        assertThat(resultado.getValor()).isEqualByComparingTo("33.33");
    }

    @Test
    @DisplayName("Deve lançar exceção ao dividir por zero")
    void deveLancarExcecaoAoDividirPorZero() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.00"));

        assertThatThrownBy(() -> dinheiro.dividir(BigDecimal.ZERO))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Divisão por zero");
    }

    @Test
    @DisplayName("Deve verificar se valor é maior que outro")
    void deveVerificarSeValorEMaiorQueOutro() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("100.00"));
        Dinheiro d2 = new Dinheiro(new BigDecimal("50.00"));

        assertThat(d1.isMaiorQue(d2)).isTrue();
        assertThat(d2.isMaiorQue(d1)).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se valor é menor que outro")
    void deveVerificarSeValorEMenorQueOutro() {
        Dinheiro d1 = new Dinheiro(new BigDecimal("50.00"));
        Dinheiro d2 = new Dinheiro(new BigDecimal("100.00"));

        assertThat(d1.isMenorQue(d2)).isTrue();
        assertThat(d2.isMenorQue(d1)).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se valor é zero")
    void deveVerificarSeValorEZero() {
        Dinheiro zero = new Dinheiro(BigDecimal.ZERO);
        Dinheiro naoZero = new Dinheiro(new BigDecimal("100.00"));

        assertThat(zero.isZero()).isTrue();
        assertThat(naoZero.isZero()).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se valor é positivo")
    void deveVerificarSeValorEPositivo() {
        Dinheiro positivo = new Dinheiro(new BigDecimal("100.00"));
        Dinheiro negativo = new Dinheiro(new BigDecimal("-50.00"));
        Dinheiro zero = new Dinheiro(BigDecimal.ZERO);

        assertThat(positivo.isPositivo()).isTrue();
        assertThat(negativo.isPositivo()).isFalse();
        assertThat(zero.isPositivo()).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se valor é negativo")
    void deveVerificarSeValorENegativo() {
        Dinheiro negativo = new Dinheiro(new BigDecimal("-50.00"));
        Dinheiro positivo = new Dinheiro(new BigDecimal("100.00"));
        Dinheiro zero = new Dinheiro(BigDecimal.ZERO);

        assertThat(negativo.isNegativo()).isTrue();
        assertThat(positivo.isNegativo()).isFalse();
        assertThat(zero.isNegativo()).isFalse();
    }

    @Test
    @DisplayName("Deve formatar moeda em reais")
    void deveFormatarMoedaEmReais() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("1234.56"));

        String formatado = dinheiro.formatarMoeda();

        assertThat(formatado).contains("1.234,56");
        assertThat(formatado).containsAnyOf("R$", "BRL");
    }

    @Test
    @DisplayName("Deve retornar moeda formatada no toString")
    void deveRetornarMoedaFormatadaNoToString() {
        Dinheiro dinheiro = new Dinheiro(new BigDecimal("100.50"));

        String resultado = dinheiro.toString();

        assertThat(resultado).contains("100,50");
        assertThat(resultado).containsAnyOf("R$", "BRL");
    }
}
