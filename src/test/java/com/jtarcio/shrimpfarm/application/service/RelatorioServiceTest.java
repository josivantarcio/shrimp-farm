package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
import com.jtarcio.shrimpfarm.application.dto.response.RelatorioCustoLoteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RelatorioService")
class RelatorioServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ViveiroRepository viveiroRepository;

    @Mock
    private BiometriaRepository biometriaRepository;

    @Mock
    private RacaoRepository racaoRepository;

    @Mock
    private NutrienteRepository nutrienteRepository;

    @Mock
    private FertilizacaoRepository fertilizacaoRepository;

    @Mock
    private CustoVariavelRepository custoVariavelRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private Viveiro viveiro;
    private Lote loteAtivo;
    private Biometria ultimaBiometria;

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
                .dataPovoamento(LocalDate.now().minusDays(60))
                .status(StatusLoteEnum.ATIVO)
                .quantidadePosLarvas(100_000)
                .build();

        ultimaBiometria = Biometria.builder()
                .id(1L)
                .lote(loteAtivo)
                .diaCultivo(60)
                .pesoMedio(new BigDecimal("15.50"))
                .biomassaEstimada(new BigDecimal("1500.00"))   // kg
                .sobrevivenciaEstimada(new BigDecimal("85.00")) // %
                .build();
    }

    @Test
    @DisplayName("obterKPIsDashboard() deve retornar KPIs zerados quando não há lotes ativos")
    void obterKPIsDashboardDeveRetornarZeradoQuandoSemLotesAtivos() {
        when(loteRepository.findByStatus(StatusLoteEnum.ATIVO)).thenReturn(List.of());

        DashboardKPIsResponse kpis = relatorioService.obterKPIsDashboard();

        assertNotNull(kpis);
        assertEquals(0, kpis.getTotalLotesAtivos());
        assertEquals(0, kpis.getTotalViveirosOcupados());
    }

    @Test
    @DisplayName("obterKPIsDashboard() deve calcular totais e médias quando há lotes ativos")
    void obterKPIsDashboardDeveCalcularMedias() {
        when(loteRepository.findByStatus(StatusLoteEnum.ATIVO))
                .thenReturn(List.of(loteAtivo));
        when(viveiroRepository.countByStatus(StatusViveiroEnum.OCUPADO))
                .thenReturn(1L);
        when(biometriaRepository.findUltimaBiometriaByLoteId(10L))
                .thenReturn(Optional.of(ultimaBiometria));

        DashboardKPIsResponse kpis = relatorioService.obterKPIsDashboard();

        assertNotNull(kpis);
        assertEquals(1, kpis.getTotalLotesAtivos());
        assertEquals(1, kpis.getTotalViveirosOcupados());
        assertEquals(ultimaBiometria.getBiomassaEstimada(), kpis.getBiomassaTotalAtual());
        assertEquals(ultimaBiometria.getPesoMedio(), kpis.getPesoMedioAtual());
        assertEquals(ultimaBiometria.getSobrevivenciaEstimada(), kpis.getTaxaSobrevivenciaMedia());
    }

    @Test
    @DisplayName("gerarRelatorioCustoLote() deve lançar EntityNotFoundException quando lote não existe")
    void gerarRelatorioCustoLoteDeveLancarEntityNotFoundQuandoLoteNaoExiste() {
        when(loteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> relatorioService.gerarRelatorioCustoLote(10L));
    }

    @Test
    @DisplayName("gerarRelatorioCustoLote() deve calcular custos, biomassa, custo/kg e FCA")
    void gerarRelatorioCustoLoteDeveCalcularIndicadores() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(biometriaRepository.findUltimaBiometriaByLoteId(10L))
                .thenReturn(Optional.of(ultimaBiometria));

        when(racaoRepository.calcularCustoTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("20000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L))
                .thenReturn(new BigDecimal("3000.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1000.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L))
                .thenReturn(new BigDecimal("2500.00"));

        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(10L))
                .thenReturn(new BigDecimal("1800.00")); // kg

        RelatorioCustoLoteResponse relatorio = relatorioService.gerarRelatorioCustoLote(10L);

        assertNotNull(relatorio);
        assertEquals(10L, relatorio.getLoteId());
        assertEquals(loteAtivo.getCodigo(), relatorio.getLoteCodigo());
        assertEquals(viveiro.getNome(), relatorio.getViveiroNome());

        BigDecimal custoTotal = new BigDecimal("20000.00")
                .add(new BigDecimal("3000.00"))
                .add(new BigDecimal("1000.00"))
                .add(new BigDecimal("2500.00"));

        assertEquals(custoTotal, relatorio.getCustoTotal());
        assertEquals(ultimaBiometria.getBiomassaEstimada(), relatorio.getBiomassaAtual());
        assertEquals(ultimaBiometria.getPesoMedio(), relatorio.getPesoMedioAtual());

        BigDecimal custoPorKg = custoTotal
                .divide(ultimaBiometria.getBiomassaEstimada(), 2, java.math.RoundingMode.HALF_UP);
        assertEquals(custoPorKg, relatorio.getCustoPorKg());

        BigDecimal fca = new BigDecimal("1800.00")
                .divide(ultimaBiometria.getBiomassaEstimada(), 2, java.math.RoundingMode.HALF_UP);
        assertEquals(fca, relatorio.getFca());
    }

    @Test
    @DisplayName("gerarRelatorioCustoLote() deve tratar nulos de custos como ZERO")
    void gerarRelatorioCustoLoteDeveTratarNulosComoZero() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(biometriaRepository.findUltimaBiometriaByLoteId(10L))
                .thenReturn(Optional.of(ultimaBiometria));

        when(racaoRepository.calcularCustoTotalRacaoByLoteId(10L)).thenReturn(null);
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(10L)).thenReturn(null);
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(10L)).thenReturn(null);
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(10L)).thenReturn(null);
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(10L)).thenReturn(null);

        RelatorioCustoLoteResponse relatorio = relatorioService.gerarRelatorioCustoLote(10L);

        assertEquals(BigDecimal.ZERO, relatorio.getCustoRacao());
        assertEquals(BigDecimal.ZERO, relatorio.getCustoNutrientes());
        assertEquals(BigDecimal.ZERO, relatorio.getCustoFertilizacao());
        assertEquals(BigDecimal.ZERO, relatorio.getCustosVariaveis());
        assertEquals(BigDecimal.ZERO, relatorio.getCustoTotal());
    }

    @Test
    @DisplayName("listarRelatoriosLotesAtivos() deve gerar relatórios para todos os lotes ativos")
    void listarRelatoriosLotesAtivosDeveGerarParaTodosLotes() {
        Lote outroLote = Lote.builder()
                .id(20L)
                .codigo("LOTE02_2025")
                .viveiro(viveiro)
                .dataPovoamento(LocalDate.now().minusDays(45))
                .status(StatusLoteEnum.ATIVO)
                .quantidadePosLarvas(80_000)
                .build();

        when(loteRepository.findByStatus(StatusLoteEnum.ATIVO))
                .thenReturn(List.of(loteAtivo, outroLote));

        // Para simplificar, stub do método interno gerarRelatorioCustoLote usando spy parcial não é necessário:
        // apenas mocamos chamadas esperadas de repositórios e deixamos o método executar.

        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteAtivo));
        when(loteRepository.findById(20L)).thenReturn(Optional.of(outroLote));

        when(biometriaRepository.findUltimaBiometriaByLoteId(anyLong()))
                .thenReturn(Optional.of(ultimaBiometria));

        when(racaoRepository.calcularCustoTotalRacaoByLoteId(anyLong()))
                .thenReturn(new BigDecimal("1000.00"));
        when(nutrienteRepository.calcularCustoTotalNutrientesByLoteId(anyLong()))
                .thenReturn(new BigDecimal("500.00"));
        when(fertilizacaoRepository.calcularCustoTotalFertilizacaoByLoteId(anyLong()))
                .thenReturn(new BigDecimal("300.00"));
        when(custoVariavelRepository.calcularCustoTotalVariavelByLoteId(anyLong()))
                .thenReturn(new BigDecimal("200.00"));
        when(racaoRepository.calcularQuantidadeTotalRacaoByLoteId(anyLong()))
                .thenReturn(new BigDecimal("800.00"));

        List<RelatorioCustoLoteResponse> relatorios =
                relatorioService.listarRelatoriosLotesAtivos();

        assertEquals(2, relatorios.size());
        assertTrue(relatorios.stream().anyMatch(r -> r.getLoteId().equals(10L)));
        assertTrue(relatorios.stream().anyMatch(r -> r.getLoteId().equals(20L)));
    }
}
