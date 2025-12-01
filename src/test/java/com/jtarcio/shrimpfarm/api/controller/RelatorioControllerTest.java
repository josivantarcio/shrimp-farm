package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
import com.jtarcio.shrimpfarm.application.dto.response.RelatorioCustoLoteResponse;
import com.jtarcio.shrimpfarm.application.service.RelatorioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RelatorioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelatorioService relatorioService;

    private RelatorioCustoLoteResponse criarRelatorioCustoLoteResponse() {
        return RelatorioCustoLoteResponse.builder()
                .loteId(1L)
                .loteCodigo("L001")
                .viveiroNome("Viveiro 1")
                .diasCultivo(90)
                .custoRacao(BigDecimal.valueOf(5000.00))
                .custoNutrientes(BigDecimal.valueOf(800.00))
                .custoFertilizacao(BigDecimal.valueOf(600.00))
                .custosVariaveis(BigDecimal.valueOf(600.00))
                .custoTotal(BigDecimal.valueOf(7000.00))
                .biomassaAtual(BigDecimal.valueOf(500))
                .pesoMedioAtual(BigDecimal.valueOf(15))
                .quantidadeEstimada(30000)
                .custoPorKg(BigDecimal.valueOf(14.00))
                .fca(BigDecimal.valueOf(1.25))
                .taxaSobrevivencia(BigDecimal.valueOf(85.5))
                .build();
    }

    private DashboardKPIsResponse criarDashboardKPIsResponse() {
        return DashboardKPIsResponse.builder()
                .totalLotesAtivos(3)
                .totalViveirosOcupados(5)
                .diasMediosCultivo(BigDecimal.valueOf(80))
                .pesoMedioAtual(BigDecimal.valueOf(12.5))
                .biomassaTotalAtual(BigDecimal.valueOf(1500))
                .custoMedioPorKg(BigDecimal.valueOf(11.50))
                .lucroMedioPorKg(BigDecimal.valueOf(4.20))
                .taxaSobrevivenciaMedia(BigDecimal.valueOf(87.3))
                .fcaMedia(BigDecimal.valueOf(1.25))
                .build();
    }

    @Test
    @DisplayName("Deve retornar relatório de custo por lote")
    void deveRetornarRelatorioCustoLote() throws Exception {
        RelatorioCustoLoteResponse response = criarRelatorioCustoLoteResponse();
        when(relatorioService.gerarRelatorioCustoLote(anyLong())).thenReturn(response);

        mockMvc.perform(get("/v1/relatorios/lotes/1/custos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loteId").value(1L))
                .andExpect(jsonPath("$.loteCodigo").value("L001"))
                .andExpect(jsonPath("$.custoTotal").value(7000.00));
    }

    @Test
    @DisplayName("Deve retornar KPIs do dashboard")
    void deveRetornarDashboardKpis() throws Exception {
        DashboardKPIsResponse response = criarDashboardKPIsResponse();
        when(relatorioService.obterKPIsDashboard()).thenReturn(response);

        mockMvc.perform(get("/v1/relatorios/dashboard")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(3))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(5))
                .andExpect(jsonPath("$.taxaSobrevivenciaMedia").value(87.3));
    }

    @Test
    @DisplayName("Deve listar relatórios de lotes ativos")
    void deveListarRelatoriosLotesAtivos() throws Exception {
        RelatorioCustoLoteResponse relatorio = criarRelatorioCustoLoteResponse();
        when(relatorioService.listarRelatoriosLotesAtivos())
                .thenReturn(List.of(relatorio));

        mockMvc.perform(get("/v1/relatorios/lotes/ativos/custos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loteId").value(1L))
                .andExpect(jsonPath("$[0].custoTotal").value(7000.00));
    }
}
