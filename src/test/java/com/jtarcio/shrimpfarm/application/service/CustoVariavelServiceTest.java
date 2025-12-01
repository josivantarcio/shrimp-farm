package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.CustoVariavelRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CustoVariavelResponse;
import com.jtarcio.shrimpfarm.application.mapper.CustoVariavelMapper;
import com.jtarcio.shrimpfarm.domain.entity.CustoVariavel;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CustoVariavelRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CustoVariavelService")
class CustoVariavelServiceTest {

    @Mock
    private CustoVariavelRepository custoVariavelRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private CustoVariavelMapper custoVariavelMapper;

    @InjectMocks
    private CustoVariavelService custoVariavelService;

    private Lote lote;
    private CustoVariavelRequest request;
    private CustoVariavel custoVariavel;
    private CustoVariavelResponse response;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .build();

        request = CustoVariavelRequest.builder()
                .loteId(10L)
                .dataLancamento(LocalDate.of(2025, 2, 1))
                .categoria(CategoriaGastoEnum.ENERGIA)
                .descricao("Energia elétrica janeiro")
                .valor(new BigDecimal("1500.00"))
                .observacoes("Conta CEAL")
                .build();

        custoVariavel = CustoVariavel.builder()
                .id(1L)
                .lote(lote)
                .dataLancamento(request.getDataLancamento())
                .categoria(request.getCategoria())
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .observacoes(request.getObservacoes())
                .build();

        response = CustoVariavelResponse.builder()
                .id(custoVariavel.getId())
                .loteId(lote.getId())
                .loteCodigo(lote.getCodigo())
                .dataLancamento(custoVariavel.getDataLancamento())
                .categoria(custoVariavel.getCategoria())
                .descricao(custoVariavel.getDescricao())
                .valor(custoVariavel.getValor())
                .observacoes(custoVariavel.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve registrar custo variável para lote existente")
    void criarDeveRegistrarCustoVariavel() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(custoVariavelMapper.toEntity(request, lote)).thenReturn(custoVariavel);
        when(custoVariavelRepository.save(any(CustoVariavel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(custoVariavelMapper.toResponse(any(CustoVariavel.class))).thenReturn(response);

        CustoVariavelResponse resultado = custoVariavelService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(new BigDecimal("1500.00"), resultado.getValor());

        verify(loteRepository).findById(10L);
        verify(custoVariavelMapper).toEntity(request, lote);
        verify(custoVariavelRepository).save(any(CustoVariavel.class));
        verify(custoVariavelMapper).toResponse(any(CustoVariavel.class));
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando lote não existe")
    void criarDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> custoVariavelService.criar(request));

        verify(custoVariavelRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar custo variável quando encontrar")
    void buscarPorIdDeveRetornarQuandoEncontrar() {
        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.of(custoVariavel));
        when(custoVariavelMapper.toResponse(custoVariavel)).thenReturn(response);

        CustoVariavelResponse resultado = custoVariavelService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> custoVariavelService.buscarPorId(1L));
    }

    @Test
    @DisplayName("listarPorLote() deve retornar lista quando lote existe")
    void listarPorLoteDeveRetornarListaQuandoLoteExiste() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(custoVariavelRepository.findByLoteIdOrderByDataLancamentoAsc(10L))
                .thenReturn(List.of(custoVariavel));
        when(custoVariavelMapper.toResponse(custoVariavel)).thenReturn(response);

        var resultado = custoVariavelService.listarPorLote(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void listarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> custoVariavelService.listarPorLote(10L));

        verify(custoVariavelRepository, never()).findByLoteIdOrderByDataLancamentoAsc(anyLong());
    }

    @Test
    @DisplayName("listarPorCategoria() deve retornar lista para categoria informada")
    void listarPorCategoriaDeveRetornarLista() {
        when(custoVariavelRepository.findByCategoria(CategoriaGastoEnum.ENERGIA))
                .thenReturn(List.of(custoVariavel));
        when(custoVariavelMapper.toResponse(custoVariavel)).thenReturn(response);

        var resultado = custoVariavelService.listarPorCategoria(CategoriaGastoEnum.ENERGIA);

        assertEquals(1, resultado.size());
        assertEquals(CategoriaGastoEnum.ENERGIA, resultado.get(0).getCategoria());
    }

    @Test
    @DisplayName("calcularTotalPorLote() deve retornar total quando existir")
    void calcularTotalPorLoteDeveRetornarTotal() {
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(new BigDecimal("4321.00"));

        BigDecimal total = custoVariavelService.calcularTotalPorLote(10L);

        assertEquals(new BigDecimal("4321.00"), total);
        verify(custoVariavelRepository).calcularCustoTotalVariavelByLoteId(10L);
    }

    @Test
    @DisplayName("calcularTotalPorLote() deve retornar ZERO quando repositório devolver null")
    void calcularTotalPorLoteDeveRetornarZeroQuandoNulo() {
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(null);

        BigDecimal total = custoVariavelService.calcularTotalPorLote(10L);

        assertEquals(BigDecimal.ZERO, total);
        verify(custoVariavelRepository).calcularCustoTotalVariavelByLoteId(10L);
    }

    @Test
    @DisplayName("atualizar() deve atualizar e retornar custo variável")
    void atualizarDeveAtualizarCustoVariavel() {
        CustoVariavelRequest requestAtualizacao = CustoVariavelRequest.builder()
                .loteId(10L)
                .dataLancamento(LocalDate.of(2025, 2, 5))
                .categoria(CategoriaGastoEnum.ENERGIA)
                .descricao("Energia elétrica fevereiro")
                .valor(new BigDecimal("1700.00"))
                .observacoes("Conta CEAL atualizada")
                .build();

        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.of(custoVariavel));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        doAnswer(invocation -> {
            CustoVariavel entity = invocation.getArgument(0);
            CustoVariavelRequest req = invocation.getArgument(1);
            Lote l = invocation.getArgument(2);
            entity.setLote(l);
            entity.setDataLancamento(req.getDataLancamento());
            entity.setCategoria(req.getCategoria());
            entity.setDescricao(req.getDescricao());
            entity.setValor(req.getValor());
            entity.setObservacoes(req.getObservacoes());
            return null;
        }).when(custoVariavelMapper).updateEntity(any(CustoVariavel.class), any(CustoVariavelRequest.class), any(Lote.class));

        when(custoVariavelRepository.save(any(CustoVariavel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(custoVariavelMapper.toResponse(any(CustoVariavel.class))).thenReturn(response);

        CustoVariavelResponse resultado = custoVariavelService.atualizar(1L, requestAtualizacao);

        assertNotNull(resultado);
        verify(custoVariavelRepository).save(any(CustoVariavel.class));
        verify(custoVariavelMapper).toResponse(any(CustoVariavel.class));
    }

    @Test
    @DisplayName("deletar() deve remover custo variável existente")
    void deletarDeveRemoverCustoVariavel() {
        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.of(custoVariavel));

        custoVariavelService.deletar(1L);

        verify(custoVariavelRepository).delete(custoVariavel);
    }
}
