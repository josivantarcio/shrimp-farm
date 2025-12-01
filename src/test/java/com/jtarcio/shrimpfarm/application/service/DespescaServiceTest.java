package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.application.mapper.DespescaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CompradorRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.DespescaRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DespescaService")
class DespescaServiceTest {

    @Mock
    private DespescaRepository despescaRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @Mock
    private DespescaMapper despescaMapper;

    @InjectMocks
    private DespescaService despescaService;

    private Viveiro viveiro;
    private Lote loteAtivo;
    private Comprador comprador;
    private DespescaRequest request;
    private Despesca despesca;
    private DespescaResponse response;

    @BeforeEach
    void setUp() {
        viveiro = Viveiro.builder()
                .id(3L)
                .nome("V-03")
                .status(StatusViveiroEnum.OCUPADO)
                .build();

        loteAtivo = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .viveiro(viveiro)
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .quantidadePosLarvas(100_000)
                .status(StatusLoteEnum.ATIVO)
                .build();

        comprador = Comprador.builder()
                .id(5L)
                .nome("Comprador A")
                .build();

        request = DespescaRequest.builder()
                .loteId(10L)
                .compradorId(5L)
                .dataDespesca(LocalDate.of(2025, 3, 1))
                .pesoTotal(new BigDecimal("2000.00"))
                .quantidadeDespescada(80_000)
                .pesoMedioFinal(new BigDecimal("25.000"))
                .precoVendaKg(new BigDecimal("25.00"))
                .custoDespesca(new BigDecimal("5000.00"))
                .observacoes("Despesca total do lote")
                .build();

        despesca = Despesca.builder()
                .id(100L)
                .lote(loteAtivo)
                .comprador(comprador)
                .dataDespesca(request.getDataDespesca())
                .pesoTotal(request.getPesoTotal())
                .quantidadeDespescada(request.getQuantidadeDespescada())
                .pesoMedioFinal(request.getPesoMedioFinal())
                .precoVendaKg(request.getPrecoVendaKg())
                .custoDespesca(request.getCustoDespesca())
                .taxaSobrevivencia(new BigDecimal("80.00"))
                .receitaTotal(new BigDecimal("50000.00"))
                .observacoes(request.getObservacoes())
                .build();

        response = DespescaResponse.builder()
                .id(despesca.getId())
                .loteId(loteAtivo.getId())
                .loteCodigo(loteAtivo.getCodigo())
                .compradorId(comprador.getId())
                .compradorNome(comprador.getNome())
                .dataDespesca(despesca.getDataDespesca())
                .pesoTotal(despesca.getPesoTotal())
                .quantidadeDespescada(despesca.getQuantidadeDespescada())
                .pesoMedioFinal(despesca.getPesoMedioFinal())
                .taxaSobrevivencia(despesca.getTaxaSobrevivencia())
                .precoVendaKg(despesca.getPrecoVendaKg())
                .receitaTotal(despesca.getReceitaTotal())
                .custoDespesca(despesca.getCustoDespesca())
                .observacoes(despesca.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criar() deve registrar despesca, calcular taxa de sobrevivência e receita, e finalizar lote")
    void criarDeveRegistrarDespescaComSucesso() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(compradorRepository.findById(5L)).thenReturn(Optional.of(comprador));
        when(despescaMapper.toEntity(request, loteAtivo, comprador)).thenReturn(despesca);
        when(despescaRepository.save(any(Despesca.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(despescaMapper.toResponse(any(Despesca.class))).thenReturn(response);

        DespescaResponse resultado = despescaService.criar(request);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(StatusLoteEnum.FINALIZADO, loteAtivo.getStatus());
        assertEquals(StatusViveiroEnum.DISPONIVEL, viveiro.getStatus());

        verify(loteRepository).findById(10L);
        verify(compradorRepository).findById(5L);
        verify(despescaMapper).toEntity(request, loteAtivo, comprador);
        verify(despescaRepository).save(any(Despesca.class));
        verify(loteRepository).save(loteAtivo);
        verify(despescaMapper).toResponse(any(Despesca.class));
    }

    @Test
    @DisplayName("criar() deve lançar EntityNotFoundException quando lote não existe")
    void criarDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> despescaService.criar(request));

        verify(despescaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando lote não está ATIVO")
    void criarDeveLancarBusinessQuandoLoteNaoAtivo() {
        loteAtivo.setStatus(StatusLoteEnum.FINALIZADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> despescaService.criar(request));

        verify(despescaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando lote já tem despesca registrada")
    void criarDeveLancarBusinessQuandoLoteJaTemDespesca() {
        loteAtivo.setDespesca(despesca);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> despescaService.criar(request));

        verify(despescaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar() deve lançar BusinessException quando data despesca é antes do povoamento")
    void criarDeveLancarBusinessQuandoDataAntesPovoamento() {
        DespescaRequest reqInvalido = DespescaRequest.builder()
                .loteId(10L)
                .compradorId(5L)
                .dataDespesca(LocalDate.of(2024, 12, 31)) // antes do povoamento
                .pesoTotal(request.getPesoTotal())
                .quantidadeDespescada(request.getQuantidadeDespescada())
                .pesoMedioFinal(request.getPesoMedioFinal())
                .precoVendaKg(request.getPrecoVendaKg())
                .custoDespesca(request.getCustoDespesca())
                .observacoes("Data inválida")
                .build();

        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> despescaService.criar(reqInvalido));

        verify(despescaRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId() deve retornar despesca quando existir")
    void buscarPorIdDeveRetornarQuandoExistir() {
        when(despescaRepository.findById(100L)).thenReturn(Optional.of(despesca));
        when(despescaMapper.toResponse(despesca)).thenReturn(response);

        DespescaResponse resultado = despescaService.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não existir")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoExistir() {
        when(despescaRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> despescaService.buscarPorId(100L));
    }

    @Test
    @DisplayName("buscarPorLote() deve retornar despesca do lote quando existir")
    void buscarPorLoteDeveRetornarQuandoExistir() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(despescaRepository.findByLoteId(10L)).thenReturn(Optional.of(despesca));
        when(despescaMapper.toResponse(despesca)).thenReturn(response);

        DespescaResponse resultado = despescaService.buscarPorLote(10L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void buscarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> despescaService.buscarPorLote(10L));
    }

    @Test
    @DisplayName("atualizar() deve recalcular taxa de sobrevivência e receita")
    void atualizarDeveRecalcularTaxaESalvar() {
        DespescaRequest requestAtualizacao = DespescaRequest.builder()
                .loteId(10L)
                .compradorId(5L)
                .dataDespesca(LocalDate.of(2025, 3, 2))
                .pesoTotal(new BigDecimal("2100.00"))
                .quantidadeDespescada(85_000)
                .pesoMedioFinal(new BigDecimal("24.700"))
                .precoVendaKg(new BigDecimal("26.00"))
                .custoDespesca(new BigDecimal("5200.00"))
                .observacoes("Ajuste de pesagem")
                .build();

        when(despescaRepository.findById(100L)).thenReturn(Optional.of(despesca));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(compradorRepository.findById(5L)).thenReturn(Optional.of(comprador));

        doAnswer(invocation -> {
            Despesca entity = invocation.getArgument(0);
            DespescaRequest req = invocation.getArgument(1);
            Lote l = invocation.getArgument(2);
            Comprador c = invocation.getArgument(3);
            entity.setLote(l);
            entity.setComprador(c);
            entity.setDataDespesca(req.getDataDespesca());
            entity.setPesoTotal(req.getPesoTotal());
            entity.setQuantidadeDespescada(req.getQuantidadeDespescada());
            entity.setPesoMedioFinal(req.getPesoMedioFinal());
            entity.setPrecoVendaKg(req.getPrecoVendaKg());
            entity.setCustoDespesca(req.getCustoDespesca());
            entity.setObservacoes(req.getObservacoes());
            return null;
        }).when(despescaMapper).updateEntity(any(Despesca.class), any(DespescaRequest.class), any(Lote.class), any(Comprador.class));

        when(despescaRepository.save(any(Despesca.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(despescaMapper.toResponse(any(Despesca.class))).thenReturn(response);

        DespescaResponse resultado = despescaService.atualizar(100L, requestAtualizacao);

        assertNotNull(resultado);
        verify(despescaRepository).save(any(Despesca.class));
        verify(despescaMapper).toResponse(any(Despesca.class));
    }

    @Test
    @DisplayName("deletar() deve remover despesca e reabrir lote/viveiro")
    void deletarDeveRemoverDespescaEReabrirLote() {
        loteAtivo.setDespesca(despesca);
        loteAtivo.setStatus(StatusLoteEnum.FINALIZADO);
        viveiro.setStatus(StatusViveiroEnum.DISPONIVEL);

        when(despescaRepository.findById(100L)).thenReturn(Optional.of(despesca));

        despescaService.deletar(100L);

        assertEquals(StatusLoteEnum.ATIVO, loteAtivo.getStatus());
        assertNull(loteAtivo.getDataDespesca());
        assertEquals(StatusViveiroEnum.OCUPADO, viveiro.getStatus());

        verify(loteRepository).save(loteAtivo);
        verify(despescaRepository).delete(despesca);
    }
}
