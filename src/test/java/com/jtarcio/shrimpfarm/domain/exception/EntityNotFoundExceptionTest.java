package com.jtarcio.shrimpfarm.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityNotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem simples")
    void deveCriarExcecaoComMensagemSimples() {
        String mensagem = "Entidade não encontrada";

        EntityNotFoundException exception = new EntityNotFoundException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("Deve criar exceção com nome da entidade e ID")
    void deveCriarExcecaoComNomeEId() {
        String entidade = "Fazenda";
        Long id = 10L;

        EntityNotFoundException exception = new EntityNotFoundException(entidade, id);

        assertThat(exception.getMessage()).isEqualTo("Fazenda com ID 10 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente para diferentes entidades")
    void deveFormatarMensagemCorretamente() {
        EntityNotFoundException exception1 = new EntityNotFoundException("Lote", 5L);
        EntityNotFoundException exception2 = new EntityNotFoundException("Viveiro", 123L);
        EntityNotFoundException exception3 = new EntityNotFoundException("Usuário", 1L);

        assertThat(exception1.getMessage()).isEqualTo("Lote com ID 5 não encontrado(a)");
        assertThat(exception2.getMessage()).isEqualTo("Viveiro com ID 123 não encontrado(a)");
        assertThat(exception3.getMessage()).isEqualTo("Usuário com ID 1 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        EntityNotFoundException exception = new EntityNotFoundException("Teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve poder ser lançada com mensagem simples")
    void devePodSerLancadaComMensagemSimples() {
        assertThatThrownBy(() -> {
            throw new EntityNotFoundException("Registro não encontrado");
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Registro não encontrado");
    }

    @Test
    @DisplayName("Deve poder ser lançada com entidade e ID")
    void devePodSerLancadaComEntidadeEId() {
        assertThatThrownBy(() -> {
            throw new EntityNotFoundException("Racao", 99L);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Racao com ID 99 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve preservar stack trace quando lançada")
    void devePreservarStackTrace() {
        try {
            throw new EntityNotFoundException("Biometria", 50L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getStackTrace()).isNotEmpty();
            assertThat(e.getStackTrace()[0].getClassName())
                    .isEqualTo(this.getClass().getName());
        }
    }

    @Test
    @DisplayName("Deve aceitar ID zero")
    void deveAceitarIdZero() {
        EntityNotFoundException exception = new EntityNotFoundException("Comprador", 0L);

        assertThat(exception.getMessage()).isEqualTo("Comprador com ID 0 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve aceitar ID negativo")
    void deveAceitarIdNegativo() {
        EntityNotFoundException exception = new EntityNotFoundException("Fornecedor", -1L);

        assertThat(exception.getMessage()).isEqualTo("Fornecedor com ID -1 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve aceitar ID muito grande")
    void deveAceitarIdMuitoGrande() {
        Long idGrande = Long.MAX_VALUE;
        EntityNotFoundException exception = new EntityNotFoundException("Despesca", idGrande);

        assertThat(exception.getMessage())
                .isEqualTo(String.format("Despesca com ID %d não encontrado(a)", idGrande));
    }

    @Test
    @DisplayName("Deve permitir mensagem null no construtor simples")
    void devePermitirMensagemNull() {
        EntityNotFoundException exception = new EntityNotFoundException(null);

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Deve permitir mensagem vazia no construtor simples")
    void devePermitirMensagemVazia() {
        EntityNotFoundException exception = new EntityNotFoundException("");

        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Deve capturar exceção em contexto real de busca")
    void deveCapturaExcecaoEmContextoReal() {
        assertThatThrownBy(() -> {
            buscarLotePorId(999L);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Lote com ID 999 não encontrado(a)");
    }

    @Test
    @DisplayName("Deve formatar corretamente com nomes compostos")
    void deveFormatarCorretamenteComNomesCompostos() {
        EntityNotFoundException exception = new EntityNotFoundException("Custo Variável", 42L);

        assertThat(exception.getMessage()).isEqualTo("Custo Variável com ID 42 não encontrado(a)");
    }

    // Método auxiliar para simular busca de entidade
    private void buscarLotePorId(Long id) {
        // Simula busca que não encontra o registro
        if (id == 999L) {
            throw new EntityNotFoundException("Lote", id);
        }
    }
}
