package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.FertilizacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FertilizacaoResponse;
import com.jtarcio.shrimpfarm.application.mapper.FertilizacaoMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FertilizacaoRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
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
@DisplayName("Testes do FertilizacaoService")
class FertilizacaoServiceTest {

    @Mock
    private FertilizacaoRepository fertilizacaoRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FertilizacaoMapper fertilizacaoMapper;

    @InjectMocks
    private FertilizacaoService fertilizacaoService;

    private Lote loteAtivo;
    private Fornecedor fornecedor;
    private FertilizacaoRequest request;
    private Fertilizacao fertilizacao;
    private FertilizacaoResponse response;

    @BeforeEach
    void setUp() {
        loteAtivo = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .status(StatusLoteEnum.ATIVO)
                .build();

        fornecedor = Fornecedor.builder()
                .id(5L)
                .nome("Fornecedor Fertilizante")
                .build();

        request = FertilizacaoRequest.builder()
                .loteId(10L)
                .fornecedorId(5L)
                .dataAplicacao(LocalDate.of(2025, 1, 5))
                .produto("Calcário")
                .quantidade(new BigDecimal("50.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("2.50"))
                .finalidade("Preparação do viveiro")
                .observacoes("Dose inicial")
                .build();

        fertilizacao = Fertilizacao.builder()
                .id(1L)
                .lote(loteAtivo)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .produto(request.getProduto())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .finalidade(request.getFinalidade())
                .observacoes(request.getObservacoes())
                .custoTotal(new BigDecimal("125.00"))
                .build();

        response = FertilizacaoResponse.builder()
                .id(fertilizacao.getId())
                .loteId(loteAtivo.getId())
                .loteCodigo(loteAtivo.getCodigo())
                .fornecedorId(fornecedor.getId())
                .fornecedorNome(fornecedor.getNome())
                .dataAplicacao(fertilizacao.getDataAplicacao())
                .produto(fertilizacao.getProduto())
                .quantidade(fertilizacao.getQuantidade())
                .unidade(fertilizacao.getUnidade())
                .custoUnitario(fertilizacao.getCustoUnitario())
                .custoTotal(fertilizacao.getCustoTotal())
                .finalidade(fertilizacao.getFinalidade())
                .observacoes(fertilizacao.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve registrar fertilização em lote ATIVO")
    void criarDeveRegistrarFertilizacaoEmLoteAtivo() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(fornecedorRepository.findById(5L)).thenReturn(Optional.of(fornecedor));
        when(fertilizacaoMapper.toEntity(request, loteAtivo, fornecedor)).thenReturn(fertilizacao);
        when(fertilizacaoRepository.save(any(Fertilizacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(fertilizacaoMapper.toResponse(any(Fertilizacao.class))).thenReturn(response);

        FertilizacaoResponse resultado = fertilizacaoService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(new BigDecimal("125.00"), resultado.getCustoTotal());

        verify(loteRepository).findById(10L);
        verify(fornecedorRepository).findById(5L);
        verify(fertilizacaoMapper).toEntity(request, loteAtivo, fornecedor);
        verify(fertilizacaoRepository).save(any(Fertilizacao.class));
        verify(fertilizacaoMapper).toResponse(any(Fertilizacao.class));
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando lote não existe")
    void criarDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fertilizacaoService.criar(request));

        verify(fertilizacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando lote não é ATIVO nem PLANEJADO")
    void criarDeveLancarBusinessQuandoStatusInvalido() {
        loteAtivo.setStatus(StatusLoteEnum.CANCELADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> fertilizacaoService.criar(request));

        verify(fertilizacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar fertilização quando encontrar")
    void buscarPorIdDeveRetornarQuandoEncontrar() {
        when(fertilizacaoRepository.findById(1L)).thenReturn(Optional.of(fertilizacao));
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        FertilizacaoResponse resultado = fertilizacaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(fertilizacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> fertilizacaoService.buscarPorId(1L));
    }

    @Test
    @DisplayName("listarPorLote() deve retornar lista quando lote existe")
    void listarPorLoteDeveRetornarListaQuandoLoteExiste() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(fertilizacaoRepository.findByLoteIdOrderByDataAplicacaoAsc(10L))
                .thenReturn(List.of(fertilizacao));
        when(fertilizacaoMapper.toResponse(fertilizacao)).thenReturn(response);

        var resultado = fertilizacaoService.listarPorLote(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void listarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> fertilizacaoService.listarPorLote(10L));

        verify(fertilizacaoRepository, never()).findByLoteIdOrderByDataAplicacaoAsc(anyLong());
    }

    @Test
    @DisplayName("calcularCustoTotalPorLote() deve retornar total quando existir")
    void calcularCustoTotalPorLoteDeveRetornarTotal() {
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(new BigDecimal("321.00"));

        BigDecimal total = fertilizacaoService.calcularCustoTotalPorLote(10L);

        assertEquals(new BigDecimal("321.00"), total);
        verify(fertilizacaoRepository).calcularCustoTotalFertilizacaoByLoteId(10L);
    }

    @Test
    @DisplayName("calcularCustoTotalPorLote() deve retornar ZERO quando repositório devolve null")
    void calcularCustoTotalPorLoteDeveRetornarZeroQuandoNulo() {
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(null);

        BigDecimal total = fertilizacaoService.calcularCustoTotalPorLote(10L);

        assertEquals(BigDecimal.ZERO, total);
        verify(fertilizacaoRepository).calcularCustoTotalFertilizacaoByLoteId(10L);
    }
}
