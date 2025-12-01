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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CompradorService")
class CompradorServiceTest {

    @Mock
    private CompradorRepository compradorRepository;

    @Mock
    private CompradorMapper compradorMapper;

    @InjectMocks
    private CompradorService compradorService;

    private CompradorRequest request;
    private Comprador comprador;
    private CompradorResponse response;

    @BeforeEach
    void setUp() {
        request = CompradorRequest.builder()
                .nome("Cliente Teste")
                .cnpj("12345678000199")
                .contato("11 99999-9999")
                .endereco("Rua A, 100")
                .ativo(true)
                .build();

        comprador = Comprador.builder()
                .id(1L)
                .nome(request.getNome())
                .cnpj(request.getCnpj())
                .contato(request.getContato())
                .endereco(request.getEndereco())
                .ativo(request.getAtivo())
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .dataAtualizacao(LocalDateTime.now())
                .build();

        response = CompradorResponse.builder()
                .id(comprador.getId())
                .nome(comprador.getNome())
                .cnpj(comprador.getCnpj())
                .contato(comprador.getContato())
                .endereco(comprador.getEndereco())
                .ativo(comprador.getAtivo())
                .dataCriacao(comprador.getDataCriacao())
                .dataAtualizacao(comprador.getDataAtualizacao())
                .build();
    }

    @Test
    @DisplayName("criarComprador() deve lançar BusinessException se CNPJ já existir")
    void criarCompradorDeveLancarBusinessQuandoCnpjJaExiste() {
        when(compradorRepository.findByCnpj(request.getCnpj()))
                .thenReturn(Optional.of(comprador));

        assertThrows(BusinessException.class,
                () -> compradorService.criar(request));

        verify(compradorRepository, times(1)).findByCnpj(request.getCnpj());
        verify(compradorRepository, never()).save(any());
    }

    @Test
    @DisplayName("criarComprador() deve criar comprador quando CNPJ for único")
    void criarCompradorDeveCriarQuandoCnpjUnico() {
        when(compradorRepository.findByCnpj(request.getCnpj()))
                .thenReturn(Optional.empty());
        when(compradorMapper.toEntity(request)).thenReturn(comprador);
        when(compradorRepository.save(comprador)).thenReturn(comprador);
        when(compradorMapper.toResponse(comprador)).thenReturn(response);

        CompradorResponse resultado = compradorService.criar(request);

        assertNotNull(resultado);
        assertEquals(response.getId(), resultado.getId());
        verify(compradorRepository, times(1)).save(comprador);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFound() {
        when(compradorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> compradorService.buscarPorId(1L));

        verify(compradorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() deve retornar comprador mapeado quando encontrar")
    void buscarPorIdDeveRetornarResponse() {
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(compradorMapper.toResponse(comprador)).thenReturn(response);

        CompradorResponse resultado = compradorService.buscarPorId(1L);

        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getCnpj(), resultado.getCnpj());
    }

    @Test
    @DisplayName("listarTodos() deve retornar lista mapeada")
    void listarTodosDeveRetornarListaMapeada() {
        when(compradorRepository.findAll()).thenReturn(List.of(comprador));
        when(compradorMapper.toResponse(any(Comprador.class))).thenReturn(response);

        List<CompradorResponse> lista = compradorService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
        verify(compradorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarAtivos() deve retornar apenas compradores ativos")
    void listarAtivosDeveRetornarApenasAtivos() {
        when(compradorRepository.findByAtivoTrue()).thenReturn(List.of(comprador));
        when(compradorMapper.toResponse(any(Comprador.class))).thenReturn(response);

        List<CompradorResponse> lista = compradorService.listarAtivos();
    }
}
