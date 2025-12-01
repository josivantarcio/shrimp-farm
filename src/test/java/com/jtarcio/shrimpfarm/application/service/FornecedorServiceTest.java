package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.application.mapper.FornecedorMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
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
@DisplayName("Testes do FornecedorService")
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @InjectMocks
    private FornecedorService fornecedorService;

    private FornecedorRequest request;
    private Fornecedor fornecedor;
    private FornecedorResponse response;

    @BeforeEach
    void setUp() {
        request = FornecedorRequest.builder()
                .nome("Fornecedor Teste")
                .cnpj("11222333000144")
                .telefone("11 3333-4444")
                .email("fornecedor@teste.com")
                .endereco("Rua X, 123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01000-000")
                .contato("João")
                .observacoes("Fornecedor de ração")
                .ativo(true)
                .build();

        fornecedor = Fornecedor.builder()
                .id(1L)
                .nome(request.getNome())
                .cnpj(request.getCnpj())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .contato(request.getContato())
                .observacoes(request.getObservacoes())
                .ativo(request.getAtivo())
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .dataAtualizacao(LocalDateTime.now())
                .build();

        response = FornecedorResponse.builder()
                .id(fornecedor.getId())
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .endereco(fornecedor.getEndereco())
                .cidade(fornecedor.getCidade())
                .estado(fornecedor.getEstado())
                .cep(fornecedor.getCep())
                .contato(fornecedor.getContato())
                .observacoes(fornecedor.getObservacoes())
                .ativo(fornecedor.getAtivo())
                .dataCriacao(fornecedor.getDataCriacao())
                .dataAtualizacao(fornecedor.getDataAtualizacao())
                .build();
    }

    @Test
    @DisplayName("criarFornecedor() deve lançar BusinessException se CNPJ já existir")
    void criarFornecedorDeveLancarBusinessQuandoCnpjJaExiste() {
        when(fornecedorRepository.findByCnpj(request.getCnpj()))
                .thenReturn(Optional.of(fornecedor));

        assertThrows(BusinessException.class,
                () -> fornecedorService.criar(request));

        verify(fornecedorRepository, times(1)).findByCnpj(request.getCnpj());
        verify(fornecedorRepository, never()).save(any());
    }

    @Test
    @DisplayName("criarFornecedor() deve criar fornecedor quando CNPJ for único")
    void criarFornecedorDeveCriarQuandoCnpjUnico() {
        when(fornecedorRepository.findByCnpj(request.getCnpj()))
                .thenReturn(Optional.empty());
        when(fornecedorMapper.toEntity(request)).thenReturn(fornecedor);
        when(fornecedorRepository.save(fornecedor)).thenReturn(fornecedor);
        when(fornecedorMapper.toResponse(fornecedor)).thenReturn(response);

        FornecedorResponse resultado = fornecedorService.criar(request);

        assertNotNull(resultado);
        assertEquals(response.getId(), resultado.getId());
        verify(fornecedorRepository, times(1)).save(fornecedor);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFound() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fornecedorService.buscarPorId(1L));

        verify(fornecedorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() deve retornar fornecedor mapeado quando encontrar")
    void buscarPorIdDeveRetornarResponse() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorMapper.toResponse(fornecedor)).thenReturn(response);

        FornecedorResponse resultado = fornecedorService.buscarPorId(1L);

        assertEquals(response.getId(), resultado.getId());
        assertEquals(response.getCnpj(), resultado.getCnpj());
    }

    @Test
    @DisplayName("listarTodos() deve retornar lista mapeada")
    void listarTodosDeveRetornarListaMapeada() {
        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor));
        when(fornecedorMapper.toResponse(any(Fornecedor.class))).thenReturn(response);

        List<FornecedorResponse> lista = fornecedorService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals(response.getId(), lista.get(0).getId());
        verify(fornecedorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarAtivos() deve retornar apenas fornecedores ativos")
    void listarAtivosDeveRetornarApenasAtivos() {
        when(fornecedorRepository.findByAtivoTrue()).thenReturn(List.of(fornecedor));
        when(fornecedorMapper.toResponse(any(Fornecedor.class))).thenReturn(response);

        List<FornecedorResponse> lista = fornecedorService.listarAtivos();

        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getAtivo());
        verify(fornecedorRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("atualizar() deve lançar EntityNotFoundException se fornecedor não existir")
    void atualizarDeveLancarEntityNotFoundQuandoNaoExiste() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fornecedorService.atualizar(1L, request));

        verify(fornecedorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("atualizar() deve lançar BusinessException se CNPJ já estiver em outro fornecedor")
    void atualizarDeveLancarBusinessQuandoCnpjDeOutroFornecedor() {
        Fornecedor outro = Fornecedor.builder()
                .id(2L)
                .nome("Outro Fornecedor")
                .cnpj(request.getCnpj())
                .ativo(true)
                .build();

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.findByCnpj(request.getCnpj())).thenReturn(Optional.of(outro));

        assertThrows(BusinessException.class,
                () -> fornecedorService.atualizar(1L, request));

        verify(fornecedorRepository, times(1)).findById(1L);
        verify(fornecedorRepository, times(1)).findByCnpj(request.getCnpj());
        verify(fornecedorRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar() deve atualizar dados quando válido")
    void atualizarDeveAtualizarQuandoValido() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.findByCnpj(request.getCnpj())).thenReturn(Optional.empty());
        doNothing().when(fornecedorMapper).updateEntity(fornecedor, request);
        when(fornecedorRepository.save(fornecedor)).thenReturn(fornecedor);
        when(fornecedorMapper.toResponse(fornecedor)).thenReturn(response);

        FornecedorResponse resultado = fornecedorService.atualizar(1L, request);

        assertEquals(response.getId(), resultado.getId());
        verify(fornecedorMapper, times(1)).updateEntity(fornecedor, request);
        verify(fornecedorRepository, times(1)).save(fornecedor);
    }

    @Test
    @DisplayName("inativar() deve marcar fornecedor como inativo")
    void inativarDeveMarcarComoInativo() {
        fornecedor.setAtivo(true);
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.save(fornecedor)).thenReturn(fornecedor);

        fornecedorService.inativar(1L);

        assertFalse(fornecedor.getAtivo());
        verify(fornecedorRepository, times(1)).save(fornecedor);
    }

    @Test
    @DisplayName("deletar() deve remover fornecedor existente")
    void deletarDeveRemoverFornecedor() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

        fornecedorService.deletar(1L);

        verify(fornecedorRepository, times(1)).delete(fornecedor);
    }

    @Test
    @DisplayName("deletar() deve lançar EntityNotFoundException se fornecedor não existir")
    void deletarDeveLancarEntityNotFoundQuandoNaoExiste() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fornecedorService.deletar(1L));

        verify(fornecedorRepository, times(1)).findById(1L);
    }
}
