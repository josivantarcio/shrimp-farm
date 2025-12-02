package com.jtarcio.shrimpfarm.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnderecoTest {

    @Test
    @DisplayName("Deve criar endereço vazio com construtor padrão")
    void deveCriarEnderecoVazio() {
        Endereco endereco = new Endereco();

        assertThat(endereco.getLogradouro()).isNull();
        assertThat(endereco.getCep()).isNull();
    }

    @Test
    @DisplayName("Deve criar endereço completo")
    void deveCriarEnderecoCompleto() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "100",
                "Apto 201",
                "Centro",
                "Natal",
                "RN",
                "59000-000"
        );

        assertThat(endereco.getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(endereco.getNumero()).isEqualTo("100");
        assertThat(endereco.getComplemento()).isEqualTo("Apto 201");
        assertThat(endereco.getBairro()).isEqualTo("Centro");
        assertThat(endereco.getCidade()).isEqualTo("Natal");
        assertThat(endereco.getEstado()).isEqualTo("RN");
        assertThat(endereco.getCep()).isEqualTo("59000-000");
    }

    @Test
    @DisplayName("Deve validar CEP com 8 dígitos sem formatação")
    void deveValidarCepComOitoDigitos() {
        Endereco endereco = new Endereco();
        endereco.setCep("59000000");

        assertThat(endereco.isCepValido()).isTrue();
    }

    @Test
    @DisplayName("Deve validar CEP formatado com hífen")
    void deveValidarCepFormatado() {
        Endereco endereco = new Endereco();
        endereco.setCep("59000-000");

        assertThat(endereco.isCepValido()).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar CEP null")
    void deveRejeitarCepNull() {
        Endereco endereco = new Endereco();
        endereco.setCep(null);

        assertThat(endereco.isCepValido()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar CEP com menos de 8 dígitos")
    void deveRejeitarCepComMenosDe8Digitos() {
        Endereco endereco = new Endereco();
        endereco.setCep("5900000");

        assertThat(endereco.isCepValido()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar CEP com mais de 8 dígitos")
    void deveRejeitarCepComMaisDe8Digitos() {
        Endereco endereco = new Endereco();
        endereco.setCep("590000000");

        assertThat(endereco.isCepValido()).isFalse();
    }

    @Test
    @DisplayName("Deve formatar endereço completo com todos os campos")
    void deveFormatarEnderecoCompletoComTodosCampos() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "100",
                "Apto 201",
                "Centro",
                "Natal",
                "RN",
                "59000-000"
        );

        String esperado = "Rua das Flores, 100 - Apto 201 - Centro - Natal/RN - CEP: 59000-000";
        assertThat(endereco.getEnderecoCompleto()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Deve formatar endereço sem complemento")
    void deveFormatarEnderecoSemComplemento() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "100",
                null,
                "Centro",
                "Natal",
                "RN",
                "59000-000"
        );

        String esperado = "Rua das Flores, 100 - Centro - Natal/RN - CEP: 59000-000";
        assertThat(endereco.getEnderecoCompleto()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Deve formatar endereço com complemento vazio")
    void deveFormatarEnderecoComComplementoVazio() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "100",
                "",
                "Centro",
                "Natal",
                "RN",
                "59000-000"
        );

        String resultado = endereco.getEnderecoCompleto();
        assertThat(resultado).doesNotContain(" -  - ");
    }

    @Test
    @DisplayName("Deve formatar endereço com complemento apenas com espaços")
    void deveFormatarEnderecoComComplementoApenasEspacos() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "100",
                "   ",
                "Centro",
                "Natal",
                "RN",
                "59000-000"
        );

        String resultado = endereco.getEnderecoCompleto();
        assertThat(resultado).doesNotContain(" -    - ");
    }

    @Test
    @DisplayName("Deve formatar endereço parcial apenas com logradouro")
    void deveFormatarEnderecoParcialApenasLogradouro() {
        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua das Flores");

        assertThat(endereco.getEnderecoCompleto()).isEqualTo("Rua das Flores");
    }

    @Test
    @DisplayName("Deve formatar endereço vazio")
    void deveFormatarEnderecoVazio() {
        Endereco endereco = new Endereco();

        assertThat(endereco.getEnderecoCompleto()).isEmpty();
    }
}
