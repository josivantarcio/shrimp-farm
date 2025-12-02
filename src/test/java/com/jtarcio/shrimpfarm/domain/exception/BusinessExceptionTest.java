package com.jtarcio.shrimpfarm.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Operação de negócio inválida";

        BusinessException exception = new BusinessException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        String mensagem = "Erro ao processar operação";
        Throwable causa = new IllegalArgumentException("Argumento inválido");

        BusinessException exception = new BusinessException(mensagem, causa);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
        assertThat(exception.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        BusinessException exception = new BusinessException("Teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve poder ser lançada")
    void devePodSerLancada() {
        assertThatThrownBy(() -> {
            throw new BusinessException("Regra de negócio violada");
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("Regra de negócio violada");
    }

    @Test
    @DisplayName("Deve preservar stack trace quando lançada")
    void devePreservarStackTrace() {
        try {
            throw new BusinessException("Teste de stack trace");
        } catch (BusinessException e) {
            assertThat(e.getStackTrace()).isNotEmpty();
            assertThat(e.getStackTrace()[0].getClassName())
                    .isEqualTo(this.getClass().getName());
        }
    }

    @Test
    @DisplayName("Deve permitir causa null")
    void devePermitirCausaNull() {
        BusinessException exception = new BusinessException("Mensagem", null);

        assertThat(exception.getMessage()).isEqualTo("Mensagem");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Deve preservar causa original ao encadear exceções")
    void devePreservarCausaOriginal() {
        IllegalStateException causaRaiz = new IllegalStateException("Estado inválido");
        IllegalArgumentException causaIntermediaria = new IllegalArgumentException("Argumento inválido", causaRaiz);
        BusinessException exception = new BusinessException("Erro de negócio", causaIntermediaria);

        assertThat(exception.getCause()).isEqualTo(causaIntermediaria);
        assertThat(exception.getCause().getCause()).isEqualTo(causaRaiz);
    }

    @Test
    @DisplayName("Deve permitir mensagem null")
    void devePermitirMensagemNull() {
        BusinessException exception = new BusinessException(null);

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Deve permitir mensagem vazia")
    void devePermitirMensagemVazia() {
        BusinessException exception = new BusinessException("");

        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Deve capturar exceção em contexto real de negócio")
    void deveCapturaExcecaoEmContextoReal() {
        assertThatThrownBy(() -> {
            validarSaldoSuficiente(100.0, 150.0);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("Saldo insuficiente para realizar a operação");
    }

    // Método auxiliar para simular regra de negócio
    private void validarSaldoSuficiente(double saldo, double valor) {
        if (saldo < valor) {
            throw new BusinessException("Saldo insuficiente para realizar a operação");
        }
    }
}
