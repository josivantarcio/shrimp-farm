package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.application.mapper.RacaoMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Racao;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.RacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RacaoService")
class RacaoServiceTest {

    @Mock
    private RacaoRepository racaoRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private RacaoMapper racaoMapper;

    @InjectMocks
    private RacaoService racaoService;

    private Lote loteAtivo;
    private Fornecedor fornecedor;
    private RacaoRequest request;
    private Racao racao;
    private RacaoResponse response;

    @BeforeEach
    void setUp() {
        loteAtivo = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .status(StatusLoteEnum.ATIVO)
                .quantidadePosLarvas(100_000)
                .build();

        fornecedor = Fornecedor.builder()
                .id(5L)
                .nome("Fornecedor Ração Teste")
                .build();

        request = RacaoRequest.builder()
                .loteId(10L)
                .fornecedorId(5L)
                .dataAplicacao(LocalDate.of(2025, 1, 15))
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Marca X")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("8.50"))
                .proteinaPercentual(new BigDecimal("35.0"))
                .observacoes("Primeira ração")
                .build();

        racao = Racao.builder()
                .id(1L)
                .lote(loteAtivo)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .tipoRacao(request.getTipoRacao())
                .marca(request.getMarca())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .proteinaPercentual(request.getProteinaPercentual())
                .observacoes(request.getObservacoes())
                .build();

        response = RacaoResponse.builder()
                .id(racao.getId())
                .loteId(loteAtivo.getId())
                .loteCodigo(loteAtivo.getCodigo())
                .fornecedorId(fornecedor.getId())
                .fornecedorNome(fornecedor.getNome())
                .dataAplicacao(racao.getDataAplicacao())
                .tipoRacao(racao.getTipoRacao())
                .marca(racao.getMarca())
                .quantidade(racao.getQuantidade())
                .unidade(racao.getUnidade())
                .custoUnitario(racao.getCustoUnitario())
                .custoTotal(new BigDecimal("850.00"))
                .proteinaPercentual(racao.getProteinaPercentual())
                .observacoes(racao.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve registrar ração com sucesso em lote ATIVO")
    void criarDeveRegistrarRacaoComSucesso() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(fornecedorRepository.findById(5L)).thenReturn(Optional.of(fornecedor));
        when(racaoMapper.toEntity(request, loteAtivo, fornecedor)).thenReturn(racao);
        when(racaoRepository.save(any(Racao.class))).thenReturn(racao);
        when(racaoMapper.toResponse(racao)).thenReturn(response);

        RacaoResponse resultado = racaoService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getLoteId());
        assertEquals(new BigDecimal("850.00"), resultado.getCustoTotal());

        verify(loteRepository).findById(10L);
        verify(fornecedorRepository).findById(5L);
        verify(racaoRepository).save(any(Racao.class));
        verify(racaoMapper).toEntity(request, loteAtivo, fornecedor);
        verify(racaoMapper).toResponse(racao);
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando lote não existe")
    void criarDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> racaoService.criar(request));

        verify(racaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando lote não está ATIVO")
    void criarDeveLancarBusinessQuandoLoteNaoAtivo() {
        loteAtivo.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class, () -> racaoService.criar(request));

        verify(racaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando dataAplicacao é antes da dataPovoamento")
    void criarDeveLancarBusinessQuandoDataAntesPovoamento() {
        RacaoRequest requestDataInvalida = RacaoRequest.builder()
                .loteId(10L)
                .fornecedorId(5L)
                .dataAplicacao(LocalDate.of(2024, 12, 31)) // Antes do povoamento
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Marca X")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("8.50"))
                .proteinaPercentual(new BigDecimal("35.0"))
                .observacoes("Primeira ração")
                .build();

        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class, () -> racaoService.criar(requestDataInvalida));

        verify(racaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar ração quando encontrar")
    void buscarPorIdDeveRetornarQuandoEncontrar() {
        when(racaoRepository.findById(1L)).thenReturn(Optional.of(racao));
        when(racaoMapper.toResponse(racao)).thenReturn(response);

        RacaoResponse resultado = racaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(racaoRepository).findById(1L);
        verify(racaoMapper).toResponse(racao);
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(racaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> racaoService.buscarPorId(1L));
    }

    @Test
    @DisplayName("listarPorLote() deve retornar lista de rações quando lote existe")
    void listarPorLoteDeveRetornarListaQuandoLoteExiste() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(racaoRepository.findByLoteIdOrderByDataAplicacaoAsc(10L))
                .thenReturn(java.util.List.of(racao));
        when(racaoMapper.toResponse(racao)).thenReturn(response);

        var resultado = racaoService.listarPorLote(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(loteRepository).existsById(10L);
        verify(racaoRepository).findByLoteIdOrderByDataAplicacaoAsc(10L);
        verify(racaoMapper).toResponse(racao);
    }

    @Test
    @DisplayName("listarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void listarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> racaoService.listarPorLote(10L));

        verify(racaoRepository, never()).findByLoteIdOrderByDataAplicacaoAsc(anyLong());
    }

    @Test
    @DisplayName("calcularTotalPorLote() deve retornar total quando existir valor no repositório")
    void calcularTotalPorLoteDeveRetornarTotal() {
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("123.456"));

        BigDecimal total = racaoService.calcularTotalPorLote(10L);

        assertEquals(new BigDecimal("123.456"), total);
        verify(racaoRepository).calcularQuantidadeTotalRacaoByLoteId(10L);
    }

    @Test
    @DisplayName("calcularTotalPorLote() deve retornar ZERO quando repositório devolver null")
    void calcularTotalPorLoteDeveRetornarZeroQuandoNulo() {
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(10L))
                .thenReturn(null);

        BigDecimal total = racaoService.calcularTotalPorLote(10L);

        assertEquals(BigDecimal.ZERO, total);
        verify(racaoRepository).calcularQuantidadeTotalRacaoByLoteId(10L);
    }

    @Test
    @DisplayName("atualizar() deve atualizar ração quando existir")
    void atualizarDeveAtualizarQuandoExistir() {
        RacaoRequest requestAtualizar = request; // pode reaproveitar o mesmo
        when(racaoRepository.findById(1L)).thenReturn(Optional.of(racao));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(fornecedorRepository.findById(5L)).thenReturn(Optional.of(fornecedor));

        when(racaoRepository.save(any(Racao.class))).thenReturn(racao);
        when(racaoMapper.toResponse(racao)).thenReturn(response);

        RacaoResponse resultado = racaoService.atualizar(1L, requestAtualizar);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(racaoRepository).findById(1L);
        verify(loteRepository).findById(10L);
        verify(fornecedorRepository).findById(5L);
        verify(racaoMapper).updateEntity(racao, requestAtualizar, loteAtivo, fornecedor);
        verify(racaoRepository).save(racao);
        verify(racaoMapper).toResponse(racao);
    }

    @Test
    @DisplayName("atualizar() deve lançar EntityNotFoundException quando ração não existir")
    void atualizarDeveLancarEntityNotFoundQuandoRacaoNaoExiste() {
        when(racaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> racaoService.atualizar(1L, request));

        verify(loteRepository, never()).findById(anyLong());
        verify(fornecedorRepository, never()).findById(anyLong());
        verify(racaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deletar() deve remover ração quando existir")
    void deletarDeveRemoverQuandoExistir() {
        when(racaoRepository.findById(1L)).thenReturn(Optional.of(racao));

        racaoService.deletar(1L);

        verify(racaoRepository).findById(1L);
        verify(racaoRepository).delete(racao);
    }

    @Test
    @DisplayName("deletar() deve lançar EntityNotFoundException quando ração não existir")
    void deletarDeveLancarEntityNotFoundQuandoNaoExistir() {
        when(racaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> racaoService.deletar(1L));

        verify(racaoRepository, never()).delete(any());
    }


}
