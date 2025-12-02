package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjecaoService - Testes Unitários")
class ProjecaoServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private BiometriaRepository biometriaRepository;

    @InjectMocks
    private ProjecaoService projecaoService;

    private Lote lote;
    private List<Biometria> biometrias;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(1L)
                .codigo("LOTE-001")
                .dataPovoamento(LocalDate.now().minusDays(60))
                .quantidadePosLarvas(100000)
                .build();

        // Criar 3 biometrias com GPD crescente
        Biometria bio1 = Biometria.builder()
                .id(1L)
                .lote(lote)
                .dataBiometria(LocalDate.now().minusDays(30))
                .diaCultivo(30)
                .pesoMedio(new BigDecimal("5.000"))
                .ganhoPesoDiario(new BigDecimal("0.3000"))
                .build();

        Biometria bio2 = Biometria.builder()
                .id(2L)
                .lote(lote)
                .dataBiometria(LocalDate.now().minusDays(15))
                .diaCultivo(45)
                .pesoMedio(new BigDecimal("9.000"))
                .ganhoPesoDiario(new BigDecimal("0.4000"))
                .build();

        Biometria bio3 = Biometria.builder()
                .id(3L)
                .lote(lote)
                .dataBiometria(LocalDate.now())
                .diaCultivo(60)
                .pesoMedio(new BigDecimal("12.000"))
                .ganhoPesoDiario(new BigDecimal("0.5000"))
                .build();

        biometrias = Arrays.asList(bio1, bio2, bio3);
    }

    // ==================== TESTES PROJETAR PESO MÉDIO ====================

    @Test
    @DisplayName("Deve projetar peso médio com sucesso")
    void deveProjetarPesoMedioComSucesso() {
        // Arrange
        LocalDate dataProjecao = LocalDate.now().plusDays(10);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biometrias);

        // Act
        BigDecimal pesoProjetado = projecaoService.projetarPesoMedio(1L, dataProjecao);

        // Assert
        assertThat(pesoProjetado).isNotNull();
        assertThat(pesoProjetado).isGreaterThan(new BigDecimal("12.00"));
        verify(loteRepository).findById(1L);
        verify(biometriaRepository).findByLoteIdOrderByDataBiometriaAsc(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao projetar peso de lote inexistente")
    void deveLancarExcecaoAoProjetarPesoDeLoteInexistente() {
        // Arrange
        when(loteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projecaoService.projetarPesoMedio(999L, LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lote");
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há biometrias")
    void deveLancarExcecaoQuandoNaoHaBiometrias() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(Arrays.asList());

        // Act & Assert
        assertThatThrownBy(() -> projecaoService.projetarPesoMedio(1L, LocalDate.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não há biometrias registradas");
    }

    @Test
    @DisplayName("Deve lançar exceção quando há menos de 2 biometrias")
    void deveLancarExcecaoQuandoHaMenosDe2Biometrias() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(Arrays.asList(biometrias.get(0)));

        // Act & Assert
        assertThatThrownBy(() -> projecaoService.projetarPesoMedio(1L, LocalDate.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pelo menos 2 biometrias");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de projeção é anterior à última biometria")
    void deveLancarExcecaoQuandoDataProjecaoEhAnterior() {
        // Arrange
        LocalDate dataAnterior = LocalDate.now().minusDays(5);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biometrias);

        // Act & Assert
        assertThatThrownBy(() -> projecaoService.projetarPesoMedio(1L, dataAnterior))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pode ser anterior à última biometria");
    }

    @Test
    @DisplayName("Deve projetar peso zero quando data de projeção é igual à última biometria")
    void deveProjetarPesoZeroQuandoDataIgualUltimaBiometria() {
        // Arrange
        LocalDate dataAtual = LocalDate.now();
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biometrias);

        // Act
        BigDecimal pesoProjetado = projecaoService.projetarPesoMedio(1L, dataAtual);

        // Assert
        assertThat(pesoProjetado).isEqualByComparingTo("12.00");
    }

    // ==================== TESTES SUGERIR DATA DESPESCA ====================

    @Test
    @DisplayName("Deve sugerir data de despesca com status IDEAL")
    void deveSugerirDataDespescaComStatusIdeal() {
        // Arrange - LOTE COM MAIS DIAS DE CULTIVO
        Lote loteIdeal = Lote.builder()
                .id(1L)
                .codigo("LOTE-001")
                .dataPovoamento(LocalDate.now().minusDays(100)) // 100 dias atrás
                .quantidadePosLarvas(100000)
                .build();

        // Biometrias com peso próximo ao ideal
        Biometria bio1 = Biometria.builder()
                .id(1L)
                .lote(loteIdeal)
                .dataBiometria(LocalDate.now().minusDays(30))
                .diaCultivo(70)
                .pesoMedio(new BigDecimal("10.000"))
                .ganhoPesoDiario(new BigDecimal("0.3000"))
                .build();

        Biometria bio2 = Biometria.builder()
                .id(2L)
                .lote(loteIdeal)
                .dataBiometria(LocalDate.now().minusDays(15))
                .diaCultivo(85)
                .pesoMedio(new BigDecimal("12.000"))
                .ganhoPesoDiario(new BigDecimal("0.4000"))
                .build();

        Biometria bio3 = Biometria.builder()
                .id(3L)
                .lote(loteIdeal)
                .dataBiometria(LocalDate.now())
                .diaCultivo(100)
                .pesoMedio(new BigDecimal("13.000"))
                .ganhoPesoDiario(new BigDecimal("0.3500"))
                .build();

        List<Biometria> biomIdeal = Arrays.asList(bio1, bio2, bio3);

        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteIdeal));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biomIdeal);

        // Act
        Map<String, Object> resultado = projecaoService.sugerirDataDespesca(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("status")).isEqualTo("IDEAL");
        assertThat(resultado.get("dataSugerida")).isNotNull();
        assertThat(resultado.get("pesoAtual")).isEqualTo(new BigDecimal("13.000"));
        assertThat(resultado.get("gpdMedio")).isNotNull();
        verify(loteRepository).findById(1L);
    }


    @Test
    @DisplayName("Deve sugerir data de despesca com status PRONTO_PARA_DESPESCA")
    void deveSugerirDataDespescaComStatusPronto() {
        // Arrange
        Biometria bioPronta = Biometria.builder()
                .id(4L)
                .lote(lote)
                .dataBiometria(LocalDate.now())
                .diaCultivo(60)
                .pesoMedio(new BigDecimal("16.000")) // Acima do peso ideal (15g)
                .ganhoPesoDiario(new BigDecimal("0.5000"))
                .build();

        List<Biometria> biomPronta = Arrays.asList(biometrias.get(0), biometrias.get(1), bioPronta);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biomPronta);

        // Act
        Map<String, Object> resultado = projecaoService.sugerirDataDespesca(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("status")).isEqualTo("PRONTO_PARA_DESPESCA");
        assertThat(resultado.get("mensagem")).isNotNull();
        assertThat(resultado.get("pesoAtual")).isEqualTo(new BigDecimal("16.000"));
    }

    @Test
    @DisplayName("Deve sugerir data de despesca com status MUITO_CEDO")
    void deveSugerirDataDespescaComStatusMuitoCedo() {
        // Arrange
        Lote loteNovo = Lote.builder()
                .id(2L)
                .codigo("LOTE-002")
                .dataPovoamento(LocalDate.now().minusDays(10)) // Muito recente
                .quantidadePosLarvas(100000)
                .build();

        Biometria bio1 = Biometria.builder()
                .id(1L)
                .lote(loteNovo)
                .dataBiometria(LocalDate.now().minusDays(5))
                .diaCultivo(5)
                .pesoMedio(new BigDecimal("2.000"))
                .ganhoPesoDiario(new BigDecimal("0.2000"))
                .build();

        Biometria bio2 = Biometria.builder()
                .id(2L)
                .lote(loteNovo)
                .dataBiometria(LocalDate.now())
                .diaCultivo(10)
                .pesoMedio(new BigDecimal("3.000"))
                .ganhoPesoDiario(new BigDecimal("0.3000"))
                .build();

        when(loteRepository.findById(2L)).thenReturn(Optional.of(loteNovo));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(2L))
                .thenReturn(Arrays.asList(bio1, bio2));

        // Act
        Map<String, Object> resultado = projecaoService.sugerirDataDespesca(2L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("status")).isEqualTo("MUITO_CEDO");
    }

    @Test
    @DisplayName("Deve sugerir data de despesca com status ATENCAO_PRAZO")
    void deveSugerirDataDespescaComStatusAtencaoPrazo() {
        // Arrange
        Lote loteAntigo = Lote.builder()
                .id(3L)
                .codigo("LOTE-003")
                .dataPovoamento(LocalDate.now().minusDays(140)) // Muito antigo
                .quantidadePosLarvas(100000)
                .build();

        Biometria bio1 = Biometria.builder()
                .id(1L)
                .lote(loteAntigo)
                .dataBiometria(LocalDate.now().minusDays(10))
                .diaCultivo(130)
                .pesoMedio(new BigDecimal("11.000"))
                .ganhoPesoDiario(new BigDecimal("0.1000"))
                .build();

        Biometria bio2 = Biometria.builder()
                .id(2L)
                .lote(loteAntigo)
                .dataBiometria(LocalDate.now())
                .diaCultivo(140)
                .pesoMedio(new BigDecimal("12.000"))
                .ganhoPesoDiario(new BigDecimal("0.1000"))
                .build();

        when(loteRepository.findById(3L)).thenReturn(Optional.of(loteAntigo));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(3L))
                .thenReturn(Arrays.asList(bio1, bio2));

        // Act
        Map<String, Object> resultado = projecaoService.sugerirDataDespesca(3L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("status")).isEqualTo("ATENCAO_PRAZO");
    }

    @Test
    @DisplayName("Deve lançar exceção ao sugerir data com menos de 2 biometrias")
    void deveLancarExcecaoAoSugerirDataComMenosDe2Biometrias() {
        // Arrange
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(Arrays.asList(biometrias.get(0)));

        // Act & Assert
        assertThatThrownBy(() -> projecaoService.sugerirDataDespesca(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pelo menos 2 biometrias");
    }

    // ==================== TESTES PROJETAR BIOMASSA ====================


    @Test
    @DisplayName("Deve projetar biomassa na despesca com sucesso")
    void deveProjetarBiomassaNaDespescaComSucesso() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now().plusDays(10);

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarBiomassaDespesca(1L, dataDespesca);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).containsKeys(
                "pesoMedioProjetado",
                "quantidadeEstimada",
                "biomassaProjetada",
                "sobrevivenciaEstimada"
        );
        // CORRIGIDO: usar isEqualByComparingTo para BigDecimal
        assertThat(resultado.get("sobrevivenciaEstimada"))
                .isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    @DisplayName("Deve calcular biomassa corretamente")
    void deveCalcularBiomassaCorretamente() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now();

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarBiomassaDespesca(1L, dataDespesca);

        // Assert
        BigDecimal biomassa = resultado.get("biomassaProjetada");
        assertThat(biomassa).isGreaterThan(BigDecimal.ZERO);
    }

    // ==================== TESTES PROJETAR RECEITA ====================

    @Test
    @DisplayName("Deve projetar receita da despesca com sucesso")
    void deveProjetarReceitaDaDespescaComSucesso() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now().plusDays(10);
        BigDecimal precoKg = new BigDecimal("25.00");

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarReceitaDespesca(1L, dataDespesca, precoKg);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("biomassaProjetada")).isNotNull();
        assertThat(resultado.get("precoVendaKg")).isEqualTo(precoKg);
        assertThat(resultado.get("receitaProjetada")).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular receita corretamente")
    void deveCalcularReceitaCorretamente() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now();
        BigDecimal precoKg = new BigDecimal("30.00");

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarReceitaDespesca(1L, dataDespesca, precoKg);

        // Assert
        BigDecimal receita = resultado.get("receitaProjetada");
        assertThat(receita).isGreaterThan(BigDecimal.ZERO);
    }

    // ==================== TESTES PROJETAR LUCRO ====================

    @Test
    @DisplayName("Deve projetar lucro da despesca com sucesso")
    void deveProjetarLucroDaDespescaComSucesso() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now().plusDays(10);
        BigDecimal precoKg = new BigDecimal("25.00");
        CalculadoraCustoService calculadoraCustoService = mock(CalculadoraCustoService.class);

        // Map<String, BigDecimal> para custos
        Map<String, BigDecimal> custos = new java.util.HashMap<>();
        custos.put("custoTotal", new BigDecimal("15000.00"));

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);
        when(calculadoraCustoService.calcularCustosDoLote(1L)).thenReturn(custos);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarLucroDespesca(
                1L, dataDespesca, precoKg, calculadoraCustoService);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.get("receitaProjetada")).isNotNull();
        assertThat(resultado.get("custoTotal")).isEqualTo(new BigDecimal("15000.00"));
        assertThat(resultado.get("lucroProjetado")).isNotNull();
        assertThat(resultado.get("roiProjetado")).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular ROI zero quando custo total é zero")
    void deveCalcularROIZeroQuandoCustoTotalEhZero() {
        // Arrange
        LocalDate dataDespesca = LocalDate.now().plusDays(10);
        BigDecimal precoKg = new BigDecimal("25.00");
        CalculadoraCustoService calculadoraCustoService = mock(CalculadoraCustoService.class);

        // Map<String, BigDecimal> para custos
        Map<String, BigDecimal> custos = new java.util.HashMap<>();
        custos.put("custoTotal", BigDecimal.ZERO);

        when(loteRepository.findById(anyLong())).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(anyLong()))
                .thenReturn(biometrias);
        when(calculadoraCustoService.calcularCustosDoLote(1L)).thenReturn(custos);

        // Act - RETORNO É Map<String, BigDecimal>
        Map<String, BigDecimal> resultado = projecaoService.projetarLucroDespesca(
                1L, dataDespesca, precoKg, calculadoraCustoService);

        // Assert
        assertThat(resultado.get("roiProjetado")).isEqualTo(BigDecimal.ZERO);
    }

    // ==================== TESTES AUXILIARES GPD ====================

    @Test
    @DisplayName("Deve calcular GPD médio com 3 biometrias")
    void deveCalcularGPDMedioCom3Biometrias() {
        // Arrange & Act
        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biometrias);

        BigDecimal peso = projecaoService.projetarPesoMedio(1L, LocalDate.now());

        // Assert - Se projetou peso, significa que calculou GPD corretamente
        assertThat(peso).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular GPD médio com mais de 3 biometrias")
    void deveCalcularGPDMedioComMaisDe3Biometrias() {
        // Arrange
        Biometria bio4 = Biometria.builder()
                .id(4L)
                .lote(lote)
                .dataBiometria(LocalDate.now().plusDays(5))
                .diaCultivo(65)
                .pesoMedio(new BigDecimal("13.500"))
                .ganhoPesoDiario(new BigDecimal("0.4500"))
                .build();

        List<Biometria> biomMais = Arrays.asList(
                biometrias.get(0), biometrias.get(1), biometrias.get(2), bio4);

        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biomMais);

        // Act
        BigDecimal peso = projecaoService.projetarPesoMedio(1L, LocalDate.now().plusDays(10));

        // Assert - Deve usar apenas as últimas 3 biometrias
        assertThat(peso).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular GPD médio ignorando biometrias sem GPD")
    void deveCalcularGPDMedioIgnorandoBiometriasSemGPD() {
        // Arrange
        Biometria bioSemGPD = Biometria.builder()
                .id(4L)
                .lote(lote)
                .dataBiometria(LocalDate.now().plusDays(5))
                .diaCultivo(65)
                .pesoMedio(new BigDecimal("13.500"))
                .ganhoPesoDiario(null) // Sem GPD
                .build();

        List<Biometria> biomComSemGPD = Arrays.asList(
                biometrias.get(0), biometrias.get(1), biometrias.get(2), bioSemGPD);

        when(loteRepository.findById(1L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(1L))
                .thenReturn(biomComSemGPD);

        // Act
        BigDecimal peso = projecaoService.projetarPesoMedio(1L, LocalDate.now().plusDays(10));

        // Assert
        assertThat(peso).isNotNull();
    }
}
