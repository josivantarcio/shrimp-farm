package com.jtarcio.shrimpfarm.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TelefoneTest {

    @Test
    @DisplayName("Deve criar telefone vazio com construtor padrão")
    void deveCriarTelefoneVazio() {
        Telefone telefone = new Telefone();

        assertThat(telefone.getNumero()).isNull();
    }

    @Test
    @DisplayName("Deve criar telefone com número")
    void deveCriarTelefoneComNumero() {
        Telefone telefone = new Telefone("84999887766");

        assertThat(telefone.getNumero()).isEqualTo("84999887766");
    }

    @Test
    @DisplayName("Deve validar telefone com 11 dígitos como válido")
    void deveValidarTelefoneCom11Digitos() {
        Telefone telefone = new Telefone("84999887766");

        assertThat(telefone.isValido()).isTrue();
    }

    @Test
    @DisplayName("Deve validar telefone com 10 dígitos como válido")
    void deveValidarTelefoneCom10Digitos() {
        Telefone telefone = new Telefone("8433221100");

        assertThat(telefone.isValido()).isTrue();
    }

    @Test
    @DisplayName("Deve validar telefone formatado com 11 dígitos")
    void deveValidarTelefoneFormatadoCom11Digitos() {
        Telefone telefone = new Telefone("(84) 99988-7766");

        assertThat(telefone.isValido()).isTrue();
    }

    @Test
    @DisplayName("Deve validar telefone formatado com 10 dígitos")
    void deveValidarTelefoneFormatadoCom10Digitos() {
        Telefone telefone = new Telefone("(84) 3322-1100");

        assertThat(telefone.isValido()).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar telefone null como inválido")
    void deveRejeitarTelefoneNull() {
        Telefone telefone = new Telefone(null);

        assertThat(telefone.isValido()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar telefone com menos de 10 dígitos")
    void deveRejeitarTelefoneComMenosDe10Digitos() {
        Telefone telefone = new Telefone("849998877");

        assertThat(telefone.isValido()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar telefone com mais de 11 dígitos")
    void deveRejeitarTelefoneComMaisDe11Digitos() {
        Telefone telefone = new Telefone("849998877665");

        assertThat(telefone.isValido()).isFalse();
    }

    @Test
    @DisplayName("Deve formatar telefone com 11 dígitos")
    void deveFormatarTelefoneCom11Digitos() {
        Telefone telefone = new Telefone("84999887766");

        assertThat(telefone.formatar()).isEqualTo("(84) 99988-7766");
    }

    @Test
    @DisplayName("Deve formatar telefone com 10 dígitos")
    void deveFormatarTelefoneCom10Digitos() {
        Telefone telefone = new Telefone("8433221100");

        assertThat(telefone.formatar()).isEqualTo("(84) 3322-1100");
    }

    @Test
    @DisplayName("Deve manter número original quando já formatado")
    void deveManterNumeroQuandoJaFormatado() {
        Telefone telefone = new Telefone("(84) 99988-7766");

        assertThat(telefone.formatar()).isEqualTo("(84) 99988-7766");
    }

    @Test
    @DisplayName("Deve retornar número original quando inválido")
    void deveRetornarNumeroOriginalQuandoInvalido() {
        Telefone telefone = new Telefone("123");

        assertThat(telefone.formatar()).isEqualTo("123");
    }

    @Test
    @DisplayName("Deve retornar telefone formatado no toString")
    void deveRetornarTelefoneFormatadoNoToString() {
        Telefone telefone = new Telefone("84999887766");

        assertThat(telefone.toString()).isEqualTo("(84) 99988-7766");
    }

    @Test
    @DisplayName("Deve permitir alteração do número via setter")
    void devePermitirAlteracaoDoNumero() {
        Telefone telefone = new Telefone("84999887766");
        telefone.setNumero("8433221100");

        assertThat(telefone.getNumero()).isEqualTo("8433221100");
        assertThat(telefone.formatar()).isEqualTo("(84) 3322-1100");
    }
}
