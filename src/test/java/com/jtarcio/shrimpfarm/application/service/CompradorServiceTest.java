package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.application.mapper.CompradorMapper;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CompradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompradorService - Testes Unitários")
class CompradorServiceTest {

    @Mock
    private CompradorRepository compradorRepository;

    @Mock
    private CompradorMapper compradorMapper;

    @InjectMocks
    private CompradorService compradorService;

    private Comprador comprador;
    private CompradorRequest request;
    private CompradorResponse response;

    @BeforeEach
    void setUp() {
        comprador = Comprador.builder()
                .id(1L)
                .nome("Comprador Teste")
                .cnpj("12345678901234")
                .contato("João Silva")
                .endereco("Rua Teste, 123")
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        request = CompradorRequest.builder()
                .nome("Comprador Teste")
                .cnpj("12345678901234")
                .contato("João Silva")
                .endereco("Rua Teste, 123")
                .ativo(true)
                .build();

        response = CompradorResponse.builder()
                .id(1L)
                .nome("Comprador Teste")
                .cnpj("12345678901234")
                .contato("João Silva")
                .endereco("Rua Teste, 123")
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar comprador com sucesso")
    void deveCriarCompradorComSucesso() {
        // Arrange
        when(compradorRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(compradorMapper.toEntity(request)).thenReturn(comprador);
        when(compradorRepository.save(any(Comprador.class))).thenReturn(comprador);
        when(compradorMapper.toResponse(comprador)).thenReturn(response);

        // Act
        CompradorResponse resultado = compradorService.criar(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Comprador Teste");
        assertThat(resultado.getCnpj()).isEqualTo("12345678901234");
        verify(compradorRepository).findByCnpj("12345678901234");
        verify(compradorRepository).save(any(Comprador.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar comprador com CNPJ duplicado")
    void deveLancarExcecaoAoCriarCompradorComCnpjDuplicado() {
        // Arrange
        when(compradorRepository.findByCnpj(anyString())).thenReturn(Optional.of(comprador));

        // Act & Assert
        assertThatThrownBy(() -> compradorService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um comprador com o CNPJ/CPF");

        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    @DisplayName("Deve buscar comprador por ID com sucesso")
    void deveBuscarCompradorPorIdComSucesso() {
        // Arrange
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(compradorMapper.toResponse(comprador)).thenReturn(response);

        // Act
        CompradorResponse resultado = compradorService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(compradorRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar comprador inexistente")
    void deveLancarExcecaoAoBuscarCompradorInexistente() {
        // Arrange
        when(compradorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compradorService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comprador");
    }

    @Test
    @DisplayName("Deve listar todos os compradores")
    void deveListarTodosCompradores() {
        // Arrange
        List<Comprador> compradores = Arrays.asList(comprador, comprador);
        when(compradorRepository.findAll()).thenReturn(compradores);
        when(compradorMapper.toResponse(any(Comprador.class))).thenReturn(response);

        // Act
        List<CompradorResponse> resultado = compradorService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        verify(compradorRepository).findAll();
    }

    @Test
    @DisplayName("Deve listar apenas compradores ativos")
    void deveListarApenasCompradoresAtivos() {
        // Arrange
        List<Comprador> compradoresAtivos = Arrays.asList(comprador);
        when(compradorRepository.findByAtivoTrue()).thenReturn(compradoresAtivos);
        when(compradorMapper.toResponse(any(Comprador.class))).thenReturn(response);

        // Act
        List<CompradorResponse> resultado = compradorService.listarAtivos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAtivo()).isTrue();
        verify(compradorRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve listar compradores paginados")
    void deveListarCompradoresPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comprador> page = new PageImpl<>(Arrays.asList(comprador));
        when(compradorRepository.findAll(pageable)).thenReturn(page);
        when(compradorMapper.toResponse(any(Comprador.class))).thenReturn(response);

        // Act
        Page<CompradorResponse> resultado = compradorService.listarPaginado(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(compradorRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar comprador com sucesso")
    void deveAtualizarCompradorComSucesso() {
        // Arrange
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.findByCnpj(anyString())).thenReturn(Optional.of(comprador));
        when(compradorRepository.save(any(Comprador.class))).thenReturn(comprador);
        when(compradorMapper.toResponse(comprador)).thenReturn(response);

        // Act
        CompradorResponse resultado = compradorService.atualizar(1L, request);

        // Assert
        assertThat(resultado).isNotNull();
        verify(compradorMapper).updateEntity(comprador, request);
        verify(compradorRepository).save(comprador);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com CNPJ de outro comprador")
    void deveLancarExcecaoAoAtualizarComCnpjDeOutroComprador() {
        // Arrange
        Comprador outroComprador = Comprador.builder().id(2L).cnpj("12345678901234").build();
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.findByCnpj(anyString())).thenReturn(Optional.of(outroComprador));

        // Act & Assert
        assertThatThrownBy(() -> compradorService.atualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe outro comprador com o CNPJ/CPF");

        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    @DisplayName("Deve deletar comprador com sucesso")
    void deveDeletarCompradorComSucesso() {
        // Arrange
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));

        // Act
        compradorService.deletar(1L);

        // Assert
        verify(compradorRepository).delete(comprador);
    }

    @Test
    @DisplayName("Deve inativar comprador com sucesso")
    void deveInativarCompradorComSucesso() {
        // Arrange
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.save(any(Comprador.class))).thenReturn(comprador);

        // Act
        compradorService.inativar(1L);

        // Assert
        assertThat(comprador.getAtivo()).isFalse();
        verify(compradorRepository).save(comprador);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar comprador inexistente")
    void deveLancarExcecaoAoDeletarCompradorInexistente() {
        // Arrange
        when(compradorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compradorService.deletar(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
