package com.jtarcio.shrimpfarm.infrastructure.config;

import com.jtarcio.shrimpfarm.application.dto.response.ErrorResponse;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        // Configurar mock para retornar valor válido
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Deve tratar BusinessException com status 400")
    void deveTratarBusinessException() {
        BusinessException exception = new BusinessException("Regra de negócio violada");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Regra de negócio violada");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Deve tratar EntityNotFoundException com status 404")
    void deveTratarEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Fazenda", 10L);

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Fazenda com ID 10 não encontrado(a)");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException com status 400")
    void deveTratarIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Argumento inválido");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com erros de validação")
    void deveTratarMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error1 = new FieldError("fazendaRequest", "nome", "não pode estar vazio");
        FieldError error2 = new FieldError("fazendaRequest", "area", "deve ser maior que zero");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        // A mensagem é genérica, os detalhes dos campos podem estar em outro lugar
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos");
    }

    @Test
    @DisplayName("Deve tratar Exception genérica com status 500")
    void deveTratarExceptionGenerica() {
        Exception exception = new Exception("Erro inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        // A mensagem retornada é genérica por segurança (não expõe detalhes internos)
        assertThat(response.getBody().getMessage()).isEqualTo("Ocorreu um erro interno no servidor");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    @DisplayName("Deve incluir timestamp nas respostas de BusinessException")
    void deveIncluirTimestampNasRespostasBusinessException() {
        BusinessException exception = new BusinessException("Teste");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve incluir timestamp nas respostas de EntityNotFoundException")
    void deveIncluirTimestampNasRespostasEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Lote", 5L);

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(exception, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve tratar diferentes tipos de BusinessException")
    void deveTratarDiferentesTiposBusinessException() {
        BusinessException ex1 = new BusinessException("Saldo insuficiente");
        BusinessException ex2 = new BusinessException("Limite excedido");

        ResponseEntity<ErrorResponse> response1 = handler.handleBusinessException(ex1, webRequest);
        ResponseEntity<ErrorResponse> response2 = handler.handleBusinessException(ex2, webRequest);

        assertThat(response1.getBody().getMessage()).isEqualTo("Saldo insuficiente");
        assertThat(response2.getBody().getMessage()).isEqualTo("Limite excedido");
    }

    @Test
    @DisplayName("Deve tratar EntityNotFoundException com diferentes entidades")
    void deveTratarEntityNotFoundComDiferentesEntidades() {
        EntityNotFoundException ex1 = new EntityNotFoundException("Viveiro", 1L);
        EntityNotFoundException ex2 = new EntityNotFoundException("Racao", 99L);

        ResponseEntity<ErrorResponse> response1 = handler.handleEntityNotFoundException(ex1, webRequest);
        ResponseEntity<ErrorResponse> response2 = handler.handleEntityNotFoundException(ex2, webRequest);

        assertThat(response1.getBody().getMessage()).contains("Viveiro");
        assertThat(response1.getBody().getMessage()).contains("1");
        assertThat(response2.getBody().getMessage()).contains("Racao");
        assertThat(response2.getBody().getMessage()).contains("99");
    }

    @Test
    @DisplayName("Deve incluir path no response")
    void deveIncluirPathNoResponse() {
        BusinessException exception = new BusinessException("Erro de teste");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isNotNull();
    }
}
