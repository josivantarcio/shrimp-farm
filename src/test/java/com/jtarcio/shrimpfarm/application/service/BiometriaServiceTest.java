package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.application.mapper.BiometriaMapper;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do BiometriaService")
class BiometriaServiceTest {

    @Mock
    private BiometriaRepository biometriaRepository;

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private RacaoRepository racaoRepository;

    @Mock
    private BiometriaMapper biometriaMapper;

    @InjectMocks
    private BiometriaService biometriaService;

    private Lote loteAtivo;
    private BiometriaRequest request;
    private Biometria biometria;
    private BiometriaResponse response;

    @BeforeEach
    void setUp() {
        loteAtivo = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .status(StatusLoteEnum.ATIVO)
                .quantidadePosLarvas(100_000)
                .build();

        request = BiometriaRequest.builder()
                .loteId(10L)
                .dataBiometria(LocalDate.of(2025, 1, 15))
                .pesoMedio(new BigDecimal("10.500"))
                .quantidadeAmostrada(50)
                .pesoTotalAmostra(new BigDecimal("525.000"))
                .observacoes("Biometria inicial")
                .build();

        biometria = Biometria.builder()
                .id(1L)
                .lote(loteAtivo)
                .dataBiometria(request.getDataBiometria())
                .diaCultivo(14)
                .pesoMedio(request.getPesoMedio())
                .quantidadeAmostrada(request.getQuantidadeAmostrada())
                .pesoTotalAmostra(request.getPesoTotalAmostra())
                .ganhoPesoDiario(new BigDecimal("0.750"))
                .biomassaEstimada(new BigDecimal("800.00"))
                .sobrevivenciaEstimada(new BigDecimal("80.0"))
                .fatorConversaoAlimentar(new BigDecimal("1.30"))
                .observacoes(request.getObservacoes())
                .build();

        response = BiometriaResponse.builder()
                .id(biometria.getId())
                .loteId(loteAtivo.getId())
                .loteCodigo(loteAtivo.getCodigo())
                .dataBiometria(biometria.getDataBiometria())
                .diaCultivo(biometria.getDiaCultivo())
                .pesoMedio(biometria.getPesoMedio())
                .quantidadeAmostrada(biometria.getQuantidadeAmostrada())
                .pesoTotalAmostra(biometria.getPesoTotalAmostra())
                .ganhoPesoDiario(biometria.getGanhoPesoDiario())
                .biomassaEstimada(biometria.getBiomassaEstimada())
                .sobrevivenciaEstimada(biometria.getSobrevivenciaEstimada())
                .fatorConversaoAlimentar(biometria.getFatorConversaoAlimentar())
                .observacoes(biometria.getObservacoes())
                .build();
    }

    @Test
    @DisplayName("criarBiometria() deve criar biometria com sucesso para lote ATIVO")
    void criarBiometriaDeveCriarComSucesso() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(biometriaMapper.toEntity(request, loteAtivo)).thenReturn(biometria);

        // simula cálculo de FCA usando ração (pode ser null ou um valor qualquer)
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1040.000"));

        // salvar devolve o próprio objeto alterado
        when(biometriaRepository.save(any(Biometria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(biometriaMapper.toResponse(any(Biometria.class)))
                .thenReturn(response);

        BiometriaResponse resultado = biometriaService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getLoteId());

        verify(loteRepository).findById(10L);
        verify(biometriaMapper).toEntity(request, loteAtivo);
        verify(biometriaRepository).save(any(Biometria.class));
        verify(biometriaMapper).toResponse(any(Biometria.class));
    }

    @Test
    @DisplayName("criarBiometria() deve lançar EntityNotFoundException quando lote não existe")
    void criarBiometriaDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> biometriaService.criar(request));

        verify(biometriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criarBiometria() deve lançar BusinessException quando data é antes do povoamento")
    void criarBiometriaDeveLancarBusinessQuandoDataAntesPovoamento() {
        BiometriaRequest requestDataInvalida = BiometriaRequest.builder()
                .loteId(10L)
                .dataBiometria(LocalDate.of(2024, 12, 31)) // antes do povoamento
                .pesoMedio(new BigDecimal("10.500"))
                .quantidadeAmostrada(50)
                .pesoTotalAmostra(new BigDecimal("525.000"))
                .observacoes("Biometria inválida")
                .build();

        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));

        assertThrows(BusinessException.class,
                () -> biometriaService.criar(requestDataInvalida));

        verify(biometriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criarBiometria() deve permitir lote PLANEJADO também")
    void criarBiometriaDevePermitirLotePlanejado() {
        loteAtivo.setStatus(StatusLoteEnum.PLANEJADO);
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(biometriaMapper.toEntity(request, loteAtivo)).thenReturn(biometria);
        when(biometriaRepository.save(any(Biometria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(biometriaMapper.toResponse(any(Biometria.class))).thenReturn(response);

        BiometriaResponse resultado = biometriaService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(biometriaRepository).save(any(Biometria.class));
    }

    @Test
    @DisplayName("buscarPorId() deve retornar biometria quando encontrar")
    void buscarPorIdDeveRetornarQuandoEncontrar() {
        when(biometriaRepository.findById(1L)).thenReturn(Optional.of(biometria));
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        BiometriaResponse resultado = biometriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("buscarPorId() deve lançar EntityNotFoundException quando não encontrar")
    void buscarPorIdDeveLancarEntityNotFoundQuandoNaoEncontrar() {
        when(biometriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> biometriaService.buscarPorId(1L));
    }

    @Test
    @DisplayName("listarPorLote() deve retornar lista quando lote existe")
    void listarPorLoteDeveRetornarListaQuandoLoteExiste() {
        when(loteRepository.existsById(10L)).thenReturn(true);
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(biometria));
        when(biometriaMapper.toResponse(biometria)).thenReturn(response);

        var resultado = biometriaService.listarPorLote(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listarPorLote() deve lançar EntityNotFoundException quando lote não existe")
    void listarPorLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> biometriaService.listarPorLote(10L));
    }
}
