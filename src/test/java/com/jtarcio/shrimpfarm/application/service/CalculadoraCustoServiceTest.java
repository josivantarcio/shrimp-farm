package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import com.jtarcio.shrimpfarm.domain.enums.CriterioRateioEnergia;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.*;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CalculadoraCustoService")
class CalculadoraCustoServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private RacaoRepository racaoRepository;

    @Mock
    private NutrienteRepository nutrienteRepository;

    @Mock
    private FertilizacaoRepository fertilizacaoRepository;

    @Mock
    private CustoVariavelRepository custoVariavelRepository;

    @Mock
    private BiometriaRepository biometriaRepository;

    @InjectMocks
    private CalculadoraCustoService calculadoraCustoService;

    private Lote loteComDespesca;
    private Despesca despesca;

    @BeforeEach
    void setUp() {
        despesca = Despesca.builder()
                .id(100L)
                .pesoTotal(new BigDecimal("2000.00"))       // kg
                .quantidadeDespescada(150_000)             // camarões
                .receitaTotal(new BigDecimal("100000.00")) // R$
                .build();

        loteComDespesca = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .custoPosLarvas(new BigDecimal("5000.00"))
                .despesca(despesca)
                .diasCultivo(120)
                .build();
    }

    @Test
    @DisplayName("calcularCustosDoLote() deve somar todos os componentes de custo")
    void calcularCustosDoLoteDeveSomarComponentes() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteComDespesca));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("20000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(new BigDecimal("3000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(new BigDecimal("2500.00"));

        Map<String, BigDecimal> custos = calculadoraCustoService.calcularCustosDoLote(10L);

        assertEquals(new BigDecimal("5000.00"), custos.get("custoPosLarvas"));
        assertEquals(new BigDecimal("20000.00"), custos.get("custoRacao"));
        assertEquals(new BigDecimal("3000.00"), custos.get("custoNutrientes"));
        assertEquals(new BigDecimal("1500.00"), custos.get("custoFertilizacao"));
        assertEquals(new BigDecimal("2500.00"), custos.get("custoVariavel"));

        BigDecimal esperadoTotal = new BigDecimal("5000.00")
                .add(new BigDecimal("20000.00"))
                .add(new BigDecimal("3000.00"))
                .add(new BigDecimal("1500.00"))
                .add(new BigDecimal("2500.00"));

        assertEquals(esperadoTotal, custos.get("custoTotal"));
    }

    @Test
    @DisplayName("calcularCustosDoLote() deve lançar EntityNotFoundException quando lote não existir")
    void calcularCustosDoLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> calculadoraCustoService.calcularCustosDoLote(10L));
    }

    @Test
    @DisplayName("calcularCustoPorKg() deve retornar custoTotal dividido pelo peso total da despesca")
    void calcularCustoPorKgDeveRetornarValorCorreto() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteComDespesca));

        when(racaoRepository.calcularCustoTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("20000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(new BigDecimal("3000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(new BigDecimal("2500.00"));

        BigDecimal custoPorKg = calculadoraCustoService.calcularCustoPorKg(10L);

        BigDecimal custoTotal = new BigDecimal("5000.00")
                .add(new BigDecimal("20000.00"))
                .add(new BigDecimal("3000.00"))
                .add(new BigDecimal("1500.00"))
                .add(new BigDecimal("2500.00"));

        BigDecimal esperado = custoTotal.divide(new BigDecimal("2000.00"), 2, java.math.RoundingMode.HALF_UP);
        assertEquals(esperado, custoPorKg);
    }

    @Test
    @DisplayName("calcularCustoPorKg() deve retornar ZERO quando não há despesca")
    void calcularCustoPorKgDeveRetornarZeroQuandoSemDespesca() {
        Lote loteSemDespesca = Lote.builder()
                .id(20L)
                .custoPosLarvas(new BigDecimal("3000.00"))
                .build();

        when(loteRepository.findById(20L)).thenReturn(Optional.of(loteSemDespesca));

        BigDecimal custoPorKg = calculadoraCustoService.calcularCustoPorKg(20L);

        assertEquals(BigDecimal.ZERO, custoPorKg);
    }

    @Test
    @DisplayName("calcularROl() deve calcular custo, receita, lucro, ROI e margem")
    void calcularROIDeveRetornarIndicadoresFinanceiros() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteComDespesca));

        when(racaoRepository.calcularCustoTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("20000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(new BigDecimal("3000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(new BigDecimal("2500.00"));

        Map<String, BigDecimal> resultado = calculadoraCustoService.calcularROI(10L);

        BigDecimal custoTotal = new BigDecimal("5000.00")
                .add(new BigDecimal("20000.00"))
                .add(new BigDecimal("3000.00"))
                .add(new BigDecimal("1500.00"))
                .add(new BigDecimal("2500.00"));

        BigDecimal receitaTotal = new BigDecimal("100000.00");
        BigDecimal lucro = receitaTotal.subtract(custoTotal);

        assertEquals(custoTotal, resultado.get("custoTotal"));
        assertEquals(receitaTotal, resultado.get("receitaTotal"));
        assertEquals(lucro, resultado.get("lucro"));

        BigDecimal roi = lucro.divide(custoTotal, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        BigDecimal margem = lucro.divide(receitaTotal, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        assertEquals(roi, resultado.get("roiPercentual"));
        assertEquals(margem, resultado.get("margemLucro"));
    }

    @Test
    @DisplayName("ratearEnergiaPorPeriodo() com critério IGUALITARIO deve dividir igualmente")
    void ratearEnergiaIgualitarioDeveDividirIgual() {
        BigDecimal custoTotalEnergia = new BigDecimal("900.00");
        List<Long> lotesIds = List.of(1L, 2L, 3L);

        Map<Long, BigDecimal> rateio = calculadoraCustoService
                .ratearEnergiaPorPeriodo(custoTotalEnergia, lotesIds, CriterioRateioEnergia.IGUALITARIO);

        assertEquals(3, rateio.size());
        assertEquals(new BigDecimal("300.00"), rateio.get(1L));
        assertEquals(new BigDecimal("300.00"), rateio.get(2L));
        assertEquals(new BigDecimal("300.00"), rateio.get(3L));
    }
}
