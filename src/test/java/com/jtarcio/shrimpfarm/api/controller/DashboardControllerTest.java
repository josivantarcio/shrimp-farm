package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.response.DashboardKPIsResponse;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelatorioService relatorioService;

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
    @DisplayName("Deve retornar os KPIs do dashboard")
    void deveRetornarKPIsDashboard() throws Exception {
        DashboardKPIsResponse response = criarDashboardKPIsResponse();
        when(relatorioService.obterKPIsDashboard()).thenReturn(response);

        mockMvc.perform(get("/v1/dashboard/kpis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(3))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(5))
                .andExpect(jsonPath("$.taxaSobrevivenciaMedia").value(87.3));
    }
}
