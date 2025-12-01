package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.NutrienteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.NutrienteResponse;
import com.jtarcio.shrimpfarm.application.mapper.NutrienteMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.TipoNutrienteEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.NutrienteRepository;
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
@DisplayName("Testes do NutrienteService")
class NutrienteServiceTest {

    @Mock
    private NutrienteRepository nutrienteRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private NutrienteMapper nutrienteMapper;

    @InjectMocks
    private NutrienteService nutrienteService;

    private Lote loteAtivo;
    private Fornecedor fornecedor;
    private NutrienteRequest request;
    private Nutriente nutriente;
    private NutrienteResponse response;

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
                .nome("Fornecedor Nutriente")
                .build();

        request = NutrienteRequest.builder()
                .loteId(10L)
                .fornecedorId(5L)
                .dataAplicacao(LocalDate.of(2025, 1, 10))
                .tipoNutriente(TipoNutrienteEnum.PROBIOTICO)
                .produto("Probiótico X")
                .quantidade(new BigDecimal("10.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("50.00"))
                .observacoes("Primeira aplicação")
                .build();

        nutriente = Nutriente.builder()
                .id(1L)
                .lote(loteAtivo)
                .fornecedor(fornecedor)
                .dataAplicacao(request.getDataAplicacao())
                .tipoNutriente(request.getTipoNutriente())
                .produto(request.getProduto())
                .quantidade(request.getQuantidade())
                .unidade(request.getUnidade())
                .custoUnitario(request.getCustoUnitario())
                .custoTotal(new BigDecimal("500.00"))
                .observacoes(request.getObservacoes())
                .build();

        response = NutrienteResponse.builder()
                .id(nutriente.getId())
                .loteId(loteAtivo.getId())
                .loteCodigo(loteAtivo.getCodigo())
                .fornecedorId(fornecedor.getId())
                .fornecedorNome(fornecedor.getNome())
                .dataAplicacao(nutriente.getDataAplicacao())
                .tipoNutriente(nutriente.getTipoNutriente())
                .produto(nutriente.getProduto())
                .quantidade(nutriente.getQuantidade())
                .unidade(nutriente.getUnidade())
                .custoUnitario(nutriente.getCustoUnitario())
                .custoTotal(nutriente.getCustoTotal())
                .observacoes(nutriente.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve registrar nutriente em lote ATIVO")
    void criarDeveRegistrarNutrienteEmLoteAtivo() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(fornecedorRepository.findById(5L)).thenReturn(Optional.of(fornecedor));
        when(nutrienteMapper.toEntity(request, loteAtivo, fornecedor)).thenReturn(nutriente);
        when(nutrienteRepository.save(any(Nutriente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(nutrienteMapper.toResponse(any(Nutriente.class))).thenReturn(response);

        NutrienteResponse resultado = nutrienteService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(new BigDecimal("500.00"), resultado.getCustoTotal());

        verify(loteRepository).findById(10L);
        verify(fornecedorRepository).findById(5L);
        verify(nutrienteMapper).toEntity(request, loteAtivo, fornecedor);
        verify(nutrienteRepository).save(any(Nutriente.class));
        verify(nutrienteMapper).toResponse(any(Nutriente.class));
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando lote não existe")
    void criarDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> nutrienteService.criar(request));

        verify(nutrienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando lote não está ATIVO")
    void criarDeveLancarBusinessQuandoLoteNaoAtivo() {
        loteAtivo.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> nutrienteService.criar(request));

        verify(nutrienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando dataAplicacao é antes do povoamento")
    void criarDeveLancarBusinessQuandoDataAntesPovoamento() {
        NutrienteRequest requestInvalido = NutrienteRequest.builder()
                .loteId(10L)
                .fornecedorId(5L)
                .dataAplicacao(LocalDate.of(2024, 12, 31))
                .tipoNutriente(TipoNutrienteEnum.PROBIOTICO)
                .produto("Probiótico X")
                .quantidade(new BigDecimal("10.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("50.00"))
                .observacoes("Data inválida")
                .build();

        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> nutrienteService.criar(requestInvalido));

        verify(nutrienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar nutriente quando encontrar")
    void buscarPorIdDeveRetornarQuandoEncontrar() {
        when(nutrienteRepository.findById(1L)).thenReturn(Optional.of(nutriente));
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        NutrienteResponse resultado = nutrienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(nutrienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> nutrienteService.buscarPorId(1L));
    }

    @Test
    @DisplayName("listarPorLote() deve retornar lista quando lote existe")
    void listarPorLoteDeveRetornarListaQuandoLoteExiste() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(nutrienteRepository.findByLoteIdOrderByDataAplicacaoAsc(10L))
                .thenReturn(List.of(nutriente));
        when(nutrienteMapper.toResponse(nutriente)).thenReturn(response);

        var resultado = nutrienteService.listarPorLote(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void listarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> nutrienteService.listarPorLote(10L));

        verify(nutrienteRepository, never()).findByLoteIdOrderByDataAplicacaoAsc(anyLong());
    }

    @Test
    @DisplayName("calcularCustoTotalPorLote() deve retornar total quando existir")
    void calcularCustoTotalPorLoteDeveRetornarTotal() {
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(new BigDecimal("1234.56"));

        BigDecimal total = nutrienteService.calcularCustoTotalPorLote(10L);

        assertEquals(new BigDecimal("1234.56"), total);
        verify(nutrienteRepository).calcularCustoTotalNutrientesByLoteId(10L);
    }

    @Test
    @DisplayName("calcularCustoTotalPorLote() deve retornar ZERO quando repositório devolver null")
    void calcularCustoTotalPorLoteDeveRetornarZeroQuandoNulo() {
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(null);

        BigDecimal total = nutrienteService.calcularCustoTotalPorLote(10L);

        assertEquals(BigDecimal.ZERO, total);
        verify(nutrienteRepository).calcularCustoTotalNutrientesByLoteId(10L);
    }
}
