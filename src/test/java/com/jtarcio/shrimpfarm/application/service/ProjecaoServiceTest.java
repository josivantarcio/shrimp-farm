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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProjecaoService")
class ProjecaoServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private BiometriaRepository biometriaRepository;

    @InjectMocks
    private ProjecaoService projecaoService;

    private Lote lote;
    private Biometria bio1;
    private Biometria bio2;
    private Biometria bio3;

    @BeforeEach
    void setUp() {
        lote = Lote.builder()
                .id(10L)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .quantidadePosLarvas(100_000)
                .build();

        bio1 = Biometria.builder()
                .id(1L)
                .lote(lote)
                .dataBiometria(LocalDate.of(2025, 1, 15))
                .diaCultivo(14)
                .pesoMedio(new BigDecimal("5.00"))
                .ganhoPesoDiario(new BigDecimal("0.20"))
                .build();

        bio2 = Biometria.builder()
                .id(2L)
                .lote(lote)
                .dataBiometria(LocalDate.of(2025, 1, 30))
                .diaCultivo(29)
                .pesoMedio(new BigDecimal("9.00"))
                .ganhoPesoDiario(new BigDecimal("0.25"))
                .build();

        bio3 = Biometria.builder()
                .id(3L)
                .lote(lote)
                .dataBiometria(LocalDate.of(2025, 2, 14))
                .diaCultivo(44)
                .pesoMedio(new BigDecimal("12.00"))
                .ganhoPesoDiario(new BigDecimal("0.30"))
                .build();
    }

    @Test
    @DisplayName("projetarPesoMedio() deve lançar EntityNotFoundException quando lote não existe")
    void projetarPesoMedioDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> projecaoService.projetarPesoMedio(10L, LocalDate.of(2025, 3, 1)));
    }

    @Test
    @DisplayName("projetarPesoMedio() deve lançar BusinessException quando não há biometrias")
    void projetarPesoMedioDeveLancarBusinessQuandoSemBiometrias() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of());

        assertThrows(BusinessException.class,
                () -> projecaoService.projetarPesoMedio(10L, LocalDate.of(2025, 3, 1)));
    }

    @Test
    @DisplayName("projetarPesoMedio() deve lançar BusinessException quando há menos de 2 biometrias")
    void projetarPesoMedioDeveLancarBusinessQuandoMenosDeDuasBiometrias() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio1));

        assertThrows(BusinessException.class,
                () -> projecaoService.projetarPesoMedio(10L, LocalDate.of(2025, 3, 1)));
    }

    @Test
    @DisplayName("projetarPesoMedio() deve projetar peso usando GPD médio das últimas biometrias")
    void projetarPesoMedioDeveProjetarPeso() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio1, bio2, bio3));

        LocalDate dataProjecao = LocalDate.of(2025, 3, 1);
        long diasProjecao = java.time.temporal.ChronoUnit.DAYS
                .between(bio3.getDataBiometria(), dataProjecao); // 15 dias

        // GPD médio das últimas 3 biometrias: (0.20 + 0.25 + 0.30) / 3
        BigDecimal gpdMedio = new BigDecimal("0.75")
                .divide(BigDecimal.valueOf(3), 4, java.math.RoundingMode.HALF_UP);

        BigDecimal esperado = bio3.getPesoMedio()
                .add(gpdMedio.multiply(BigDecimal.valueOf(diasProjecao)))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal projetado = projecaoService.projetarPesoMedio(10L, dataProjecao);

        assertEquals(esperado, projetado);
    }

    @Test
    @DisplayName("sugerirDataDespesca() deve retornar PRONTO quando peso já >= ideal")
    void sugerirDataDespescaDeveRetornarProntoQuandoPesoAcimaIdeal() {
        Biometria bioPesoAlto = Biometria.builder()
                .id(4L)
                .lote(lote)
                .dataBiometria(LocalDate.of(2025, 3, 1))
                .diaCultivo(60)
                .pesoMedio(new BigDecimal("16.00")) // acima do peso ideal 15g
                .ganhoPesoDiario(new BigDecimal("0.30"))
                .build();

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio2, bioPesoAlto));

        Map<String, Object> resultado = projecaoService.sugerirDataDespesca(10L);

        assertEquals("PRONTO_PARA_DESPESCA", resultado.get("status"));
        assertEquals(bioPesoAlto.getPesoMedio(), resultado.get("pesoAtual"));
    }

    @Test
    @DisplayName("projetarBiomassaDespesca() deve projetar peso médio, quantidade e biomassa")
    void projetarBiomassaDespescaDeveCalcularBiomassa() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio2, bio3));

        LocalDate dataDespesca = LocalDate.of(2025, 3, 1);

        // Peso projetado usando a própria service (garante mesmo cálculo interno)
        BigDecimal pesoProjetado = projecaoService.projetarPesoMedio(10L, dataDespesca);

        BigDecimal quantidadeEstimada = BigDecimal.valueOf(lote.getQuantidadePosLarvas())
                .multiply(BigDecimal.valueOf(0.80)); // 80% de sobrevivência padrão

        BigDecimal biomassaEsperada = pesoProjetado
                .multiply(quantidadeEstimada)
                .divide(BigDecimal.valueOf(1000), 2, java.math.RoundingMode.HALF_UP);

        Map<String, BigDecimal> projecao =
                projecaoService.projetarBiomassaDespesca(10L, dataDespesca);

        assertEquals(pesoProjetado.setScale(2, java.math.RoundingMode.HALF_UP),
                projecao.get("pesoMedioProjetado"));
        assertEquals(quantidadeEstimada, projecao.get("quantidadeEstimada"));
        assertEquals(biomassaEsperada, projecao.get("biomassaProjetada"));

        // compara ignorando diferença de escala (80 vs 80.0)
        BigDecimal sobrevivencia = projecao.get("sobrevivenciaEstimada");
        assertEquals(0, sobrevivencia.compareTo(new BigDecimal("80")));
    }


    @Test
    @DisplayName("projetarReceitaDespesca() deve multiplicar biomassa projetada pelo preço/kg")
    void projetarReceitaDespescaDeveCalcularReceita() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio2, bio3));

        LocalDate dataDespesca = LocalDate.of(2025, 3, 1);
        BigDecimal preco = new BigDecimal("25.00");

        Map<String, BigDecimal> resultado =
                projecaoService.projetarReceitaDespesca(10L, dataDespesca, preco);

        BigDecimal biomassaProjetada = resultado.get("biomassaProjetada");
        BigDecimal receitaEsperada = biomassaProjetada.multiply(preco);

        assertEquals(preco, resultado.get("precoVendaKg"));
        assertEquals(receitaEsperada, resultado.get("receitaProjetada"));
    }

    @Test
    @DisplayName("projetarLucroDespesca() deve usar CalculadoraCustoService para custo e calcular ROI")
    void projetarLucroDespescaDeveCalcularLucroEROI() {
        CalculadoraCustoService calculadoraMock = mock(CalculadoraCustoService.class);

        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(biometriaRepository.findByLoteIdOrderByDataBiometriaAsc(10L))
                .thenReturn(List.of(bio2, bio3));

        LocalDate dataDespesca = LocalDate.of(2025, 3, 1);
        BigDecimal preco = new BigDecimal("25.00");

        Map<String, BigDecimal> projecaoReceita =
                projecaoService.projetarReceitaDespesca(10L, dataDespesca, preco);
        BigDecimal receitaProjetada = projecaoReceita.get("receitaProjetada");

        when(calculadoraMock.calcularCustosDoLote(10L))
                .thenReturn(Map.of("custoTotal", new BigDecimal("50000.00")));

        Map<String, BigDecimal> resultado =
                projecaoService.projetarLucroDespesca(10L, dataDespesca, preco, calculadoraMock);

        BigDecimal custoTotal = resultado.get("custoTotal");
        BigDecimal lucroEsperado = receitaProjetada.subtract(custoTotal);
        BigDecimal roiEsperado = lucroEsperado
                .divide(custoTotal, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        assertEquals(receitaProjetada, resultado.get("receitaProjetada"));
        assertEquals(custoTotal, resultado.get("custoTotal"));
        assertEquals(lucroEsperado, resultado.get("lucroProjetado"));
        assertEquals(roiEsperado, resultado.get("roiProjetado"));
    }
}
