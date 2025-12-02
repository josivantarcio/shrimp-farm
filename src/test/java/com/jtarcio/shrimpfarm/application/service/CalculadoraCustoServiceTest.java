package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.domain.entity.*;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import com.jtarcio.shrimpfarm.domain.enums.CriterioRateioEnergia;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    private Lote lote;
    private Despesca despesca;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(1L)
                .custoPosLarvas(new BigDecimal("10000.00"))
                .diasCultivo(90)
                .build();

        despesca = Despesca.builder()
                .id(1L)
                .lote(lote)
                .pesoTotal(new BigDecimal("500.00"))
                .quantidadeDespescada(100000)
                .receitaTotal(new BigDecimal("50000.00"))
                .build();
    }

    // ===================== calcularCustosDoLote =====================

    @Test
    @DisplayName("Deve calcular todos os custos do lote com sucesso")
    void deveCalcularTodosCustosDoLote() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("5000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("2000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(new BigDecimal("3500.00"));

        // Act
        Map<String, BigDecimal> custos = calculadoraCustoService.calcularCustosDoLote(1L);

        // Assert
        assertThat(custos).isNotNull();
        assertThat(custos.get("custoPosLarvas")).isEqualByComparingTo("10000.00");
        assertThat(custos.get("custoRacao")).isEqualByComparingTo("5000.00");
        assertThat(custos.get("custoNutrientes")).isEqualByComparingTo("2000.00");
        assertThat(custos.get("custoFertilizacao")).isEqualByComparingTo("1500.00");
        assertThat(custos.get("custoVariavel")).isEqualByComparingTo("3500.00");
        assertThat(custos.get("custoTotal")).isEqualByComparingTo("22000.00");

        verify(loteRepository).findById(1L);
        verify(racaoRepository).calcularCustoTotalRacaoByLoteId(1L);
        verify(nutrienteRepository).calcularCustoTotalNutrientesByLoteId(1L);
        verify(fertilizacaoRepository).calcularCustoTotalFertilizacaoByLoteId(1L);
        verify(custoVariavelRepository).calcularCustoTotalVariavelByLoteId(1L);
    }

    @Test
    @DisplayName("Deve calcular custos considerando valores null como zero")
    void deveCalcularCustosComValoresNull() {
        // Arrange
        Lote loteComCustoNull = Lote.builder().id(1L).custoPosLarvas(null).build();
        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteComCustoNull));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L)).thenReturn(null);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L)).thenReturn(null);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L)).thenReturn(null);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L)).thenReturn(null);

        // Act
        Map<String, BigDecimal> custos = calculadoraCustoService.calcularCustosDoLote(1L);

        // Assert
        assertThat(custos.get("custoPosLarvas")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(custos.get("custoRacao")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(custos.get("custoNutrientes")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(custos.get("custoFertilizacao")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(custos.get("custoVariavel")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(custos.get("custoTotal")).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando lote não encontrado")
    void deveLancarExcecaoQuandoLoteNaoEncontrado() {
        // Arrange
        when(loteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> calculadoraCustoService.calcularCustosDoLote(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote")
                .hasMessageContaining("999");

        verify(loteRepository).findById(999L);
    }

    // ===================== calcularCustoPorKg =====================

    @Test
    @DisplayName("Deve calcular custo por kg quando há despesca")
    void deveCalcularCustoPorKgComDespesca() {
        // Arrange
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("5000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("2000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(new BigDecimal("3500.00"));

        // Act
        BigDecimal custoPorKg = calculadoraCustoService.calcularCustoPorKg(1L);

        // Assert (22000 / 500 = 44.00)
        assertThat(custoPorKg).isEqualByComparingTo("44.00");
    }

    @Test
    @DisplayName("Deve retornar zero quando lote não tem despesca")
    void deveRetornarZeroQuandoNaoTemDespesca() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act
        BigDecimal custoPorKg = calculadoraCustoService.calcularCustoPorKg(1L);

        // Assert
        assertThat(custoPorKg).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve retornar zero quando peso total da despesca é zero")
    void deveRetornarZeroQuandoPesoTotalZero() {
        // Arrange
        despesca.setPesoTotal(BigDecimal.ZERO);
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);

        // Act
        BigDecimal custoPorKg = calculadoraCustoService.calcularCustoPorKg(1L);

        // Assert
        assertThat(custoPorKg).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ===================== calcularCustoPorCamarao =====================

    @Test
    @DisplayName("Deve calcular custo por camarão quando há despesca")
    void deveCalcularCustoPorCamaraoComDespesca() {
        // Arrange
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("5000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("2000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(new BigDecimal("3500.00"));

        // Act
        BigDecimal custoPorCamarao = calculadoraCustoService.calcularCustoPorCamarao(1L);

        // Assert (22000 / 100000 = 0.22)
        assertThat(custoPorCamarao).isEqualByComparingTo("0.2200");
    }

    @Test
    @DisplayName("Deve retornar zero quando quantidade despescada é zero")
    void deveRetornarZeroQuandoQuantidadeDespescadaZero() {
        // Arrange
        despesca.setQuantidadeDespescada(0);
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);

        // Act
        BigDecimal custoPorCamarao = calculadoraCustoService.calcularCustoPorCamarao(1L);

        // Assert
        assertThat(custoPorCamarao).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ===================== calcularPercentualCustos =====================

    @Test
    @DisplayName("Deve calcular percentual de cada custo")
    void deveCalcularPercentualCustos() {
        // Arrange
        Lote loteComCustoZero = Lote.builder().id(1L).custoPosLarvas(BigDecimal.ZERO).build();
        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteComCustoZero));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("10000.00")); // 50%
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("5000.00")); // 25%
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("3000.00")); // 15%
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(new BigDecimal("2000.00")); // 10%
        // Total: 20000 (SEM custoPosLarvas para facilitar os percentuais)

        // Act
        Map<String, BigDecimal> percentuais = calculadoraCustoService.calcularPercentualCustos(1L);

        // Assert
        assertThat(percentuais).isNotNull();
        assertThat(percentuais.get("custoRacaoPercentual"))
                .isEqualByComparingTo(new BigDecimal("50.0000"));
        assertThat(percentuais.get("custoNutrientesPercentual"))
                .isEqualByComparingTo(new BigDecimal("25.0000"));
        assertThat(percentuais.get("custoFertilizacaoPercentual"))
                .isEqualByComparingTo(new BigDecimal("15.0000"));
        assertThat(percentuais.get("custoVariavelPercentual"))
                .isEqualByComparingTo(new BigDecimal("10.0000"));
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando custo total é zero")
    void deveRetornarMapaVazioQuandoCustoTotalZero() {
        // Arrange
        Lote loteComCustoNull = Lote.builder().id(1L).build();
        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteComCustoNull));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L)).thenReturn(BigDecimal.ZERO);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L)).thenReturn(BigDecimal.ZERO);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L)).thenReturn(BigDecimal.ZERO);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L)).thenReturn(BigDecimal.ZERO);

        // Act
        Map<String, BigDecimal> percentuais = calculadoraCustoService.calcularPercentualCustos(1L);

        // Assert
        assertThat(percentuais).isEmpty();
    }

    // ===================== calcularCustoMedioDiario =====================

    @Test
    @DisplayName("Deve calcular custo médio diário")
    void deveCalcularCustoMedioDiario() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("9000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        // Total: 19000, dias: 90 -> 19000/90 = 211.11

        // Act
        BigDecimal custoMedioDiario = calculadoraCustoService.calcularCustoMedioDiario(1L);

        // Assert
        assertThat(custoMedioDiario).isEqualByComparingTo("211.11");
    }

    @Test
    @DisplayName("Deve retornar zero quando dias de cultivo é zero")
    void deveRetornarZeroQuandoDiasCultivoZero() {
        // Arrange
        lote.setDiasCultivo(0);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act
        BigDecimal custoMedioDiario = calculadoraCustoService.calcularCustoMedioDiario(1L);

        // Assert
        assertThat(custoMedioDiario).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve retornar zero quando dias de cultivo é null")
    void deveRetornarZeroQuandoDiasCultivoNull() {
        // Arrange
        lote.setDiasCultivo(null);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act
        BigDecimal custoMedioDiario = calculadoraCustoService.calcularCustoMedioDiario(1L);

        // Assert
        assertThat(custoMedioDiario).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ===================== calcularROI =====================

    @Test
    @DisplayName("Deve calcular ROI com sucesso quando há despesca")
    void deveCalcularROIComSucesso() {
        // Arrange
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(new BigDecimal("5000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(new BigDecimal("2000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(new BigDecimal("1500.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(new BigDecimal("3500.00"));

        // Act
        Map<String, BigDecimal> roi = calculadoraCustoService.calcularROI(1L);

        // Assert
        // Custo Total: 22000, Receita: 50000, Lucro: 28000
        // ROI: (28000 / 22000) * 100 = 127.27%
        // Margem: (28000 / 50000) * 100 = 56%
        assertThat(roi.get("custoTotal")).isEqualByComparingTo("22000.00");
        assertThat(roi.get("receitaTotal")).isEqualByComparingTo("50000.00");
        assertThat(roi.get("lucro")).isEqualByComparingTo("28000.00");

        // Usa comparação com tolerância para arredondamento
        BigDecimal roiPercentual = roi.get("roiPercentual");
        assertThat(roiPercentual)
                .isGreaterThanOrEqualTo(new BigDecimal("127.27"))
                .isLessThanOrEqualTo(new BigDecimal("127.28"));

        assertThat(roi.get("margemLucro")).isEqualByComparingTo("56.0000");
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando não há despesca para ROI")
    void deveRetornarMapaVazioQuandoNaoHaDespescaParaROI() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));

        // Act
        Map<String, BigDecimal> roi = calculadoraCustoService.calcularROI(1L);

        // Assert
        assertThat(roi).isEmpty();
    }

    @Test
    @DisplayName("Deve calcular ROI com receita null como zero")
    void deveCalcularROIComReceitaNull() {
        // Arrange
        despesca.setReceitaTotal(null);
        lote.setDespesca(despesca);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(racaoRepository.calcularCustoTotalRacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(1L))
                .thenReturn(BigDecimal.ZERO);

        // Act
        Map<String, BigDecimal> roi = calculadoraCustoService.calcularROI(1L);

        // Assert
        assertThat(roi.get("receitaTotal")).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(roi.get("lucro")).isEqualByComparingTo(new BigDecimal("-10000.00")); // Prejuízo
    }

    // ===================== Rateio de Energia =====================

    @Test
    @DisplayName("Deve ratear energia por dias de cultivo")
    void deveRatearEnergiaPorDiasCultivo() {
        // Arrange
        Lote lote2 = Lote.builder().id(2L).diasCultivo(60).build();
        Lote lote3 = Lote.builder().id(3L).diasCultivo(30).build();

        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote)); // 90 dias
        when(loteRepository.findById(2L)).thenReturn(Optional.of(lote2)); // 60 dias
        when(loteRepository.findById(3L)).thenReturn(Optional.of(lote3)); // 30 dias
        // Total: 180 dias

        BigDecimal custoEnergia = new BigDecimal("1800.00");
        List<Long> lotesIds = Arrays.asList(1L, 2L, 3L);

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.DIAS_CULTIVO
        );

        // Assert
        // Lote 1: (90/180) * 1800 = 900.00
        // Lote 2: (60/180) * 1800 = 600.00
        // Lote 3: (30/180) * 1800 = 300.00
        assertThat(rateio.get(1L)).isEqualByComparingTo("900.00");
        assertThat(rateio.get(2L)).isEqualByComparingTo("600.00");
        assertThat(rateio.get(3L)).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Deve ratear energia igualitariamente")
    void deveRatearEnergiaIgualitariamente() {
        // Arrange
        BigDecimal custoEnergia = new BigDecimal("3000.00");
        List<Long> lotesIds = Arrays.asList(1L, 2L, 3L);

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.IGUALITARIO
        );

        // Assert
        // 3000 / 3 = 1000.00 para cada
        assertThat(rateio.get(1L)).isEqualByComparingTo("1000.00");
        assertThat(rateio.get(2L)).isEqualByComparingTo("1000.00");
        assertThat(rateio.get(3L)).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Deve ratear energia por biomassa")
    void deveRatearEnergiaPorBiomassa() {
        // Arrange
        Biometria bio1 = Biometria.builder().lote(lote).biomassaEstimada(new BigDecimal("300")).build();
        Biometria bio2 = Biometria.builder().biomassaEstimada(new BigDecimal("200")).build();

        when(biometriaRepository.findUltimaBiometriaByLoteId(1L)).thenReturn(Optional.of(bio1));
        when(biometriaRepository.findUltimaBiometriaByLoteId(2L)).thenReturn(Optional.of(bio2));

        BigDecimal custoEnergia = new BigDecimal("5000.00");
        List<Long> lotesIds = Arrays.asList(1L, 2L);

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.BIOMASSA
        );

        // Assert
        // Total biomassa: 500kg
        // Lote 1: (300/500) * 5000 = 3000.00
        // Lote 2: (200/500) * 5000 = 2000.00
        assertThat(rateio.get(1L)).isEqualByComparingTo("3000.00");
        assertThat(rateio.get(2L)).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("Deve usar rateio igualitário quando biomassa total é zero")
    void deveUsarRateioIgualitarioQuandoBiomassaZero() {
        // Arrange
        when(biometriaRepository.findUltimaBiometriaByLoteId(1L)).thenReturn(Optional.empty());
        when(biometriaRepository.findUltimaBiometriaByLoteId(2L)).thenReturn(Optional.empty());

        BigDecimal custoEnergia = new BigDecimal("2000.00");
        List<Long> lotesIds = Arrays.asList(1L, 2L);

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.BIOMASSA
        );

        // Assert - Fallback para igualitário
        assertThat(rateio.get(1L)).isEqualByComparingTo("1000.00");
        assertThat(rateio.get(2L)).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando lista de lotes é vazia")
    void deveRetornarMapaVazioQuandoListaLotesVazia() {
        // Arrange
        BigDecimal custoEnergia = new BigDecimal("1000.00");
        List<Long> lotesIds = Collections.emptyList();

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.DIAS_CULTIVO
        );

        // Assert
        assertThat(rateio).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando custo energia é zero")
    void deveRetornarMapaVazioQuandoCustoEnergiaZero() {
        // Arrange
        BigDecimal custoEnergia = BigDecimal.ZERO;
        List<Long> lotesIds = Arrays.asList(1L, 2L);

        // Act
        Map<Long, BigDecimal> rateio = calculadoraCustoService.ratearEnergiaPorPeriodo(
                custoEnergia, lotesIds, CriterioRateioEnergia.IGUALITARIO
        );

        // Assert
        assertThat(rateio).isEmpty();
    }

    // ===================== registrarRateioEnergia =====================

    @Test
    @DisplayName("Deve registrar rateio de energia como custo variável")
    void deveRegistrarRateioEnergia() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(loteRepository.findById(2L)).thenReturn(Optional.of(
                Lote.builder().id(2L).diasCultivo(60).build()
        ));

        BigDecimal custoEnergia = new BigDecimal("1500.00");
        List<Long> lotesIds = Arrays.asList(1L, 2L);
        LocalDate dataRef = LocalDate.of(2025, 12, 1);

        // Act
        calculadoraCustoService.registrarRateioEnergia(
                custoEnergia, lotesIds, CriterioRateioEnergia.IGUALITARIO, dataRef
        );

        // Assert
        ArgumentCaptor<CustoVariavel> captor = ArgumentCaptor.forClass(CustoVariavel.class);
        verify(custoVariavelRepository, times(2)).save(captor.capture());

        List<CustoVariavel> custosCapturados = captor.getAllValues();
        assertThat(custosCapturados).hasSize(2);
        assertThat(custosCapturados.get(0).getCategoria()).isEqualTo(CategoriaGastoEnum.ENERGIA);
        assertThat(custosCapturados.get(0).getValor()).isEqualByComparingTo("750.00");
        assertThat(custosCapturados.get(0).getDataLancamento()).isEqualTo(dataRef);
    }
}
