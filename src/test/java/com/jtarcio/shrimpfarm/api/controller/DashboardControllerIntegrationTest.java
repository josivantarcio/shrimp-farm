package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - DashboardController")
class DashboardControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FazendaRepository fazendaRepository;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private LoteRepository loteRepository;

    private Fazenda fazenda;
    private Viveiro viveiro;

    @BeforeEach
    void setUp() {
        fazendaRepository.deleteAll();
        viveiroRepository.deleteAll();
        loteRepository.deleteAll();

        fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .proprietario("João Teste")
                .cidade("Natal")
                .estado("RN")
                .ativa(true)
                .build();
        fazenda = fazendaRepository.save(fazenda);

        viveiro = Viveiro.builder()
                .fazenda(fazenda)
                .codigo("V001")
                .nome("Viveiro 1")
                .status(StatusViveiroEnum.DISPONIVEL)  // ← MANTÉM DISPONÍVEL para primeiro teste
                .ativo(true)
                .build();
        viveiroRepository.save(viveiro);
    }

    @Test
    @DisplayName("Deve retornar KPIs do dashboard com sucesso")
    void deveRetornarKPIsDashboardComSucesso() throws Exception {
        mockMvc.perform(get("/v1/dashboard/kpis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(0))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(0));
    }

    @Test
    @DisplayName("Deve retornar KPIs corretos com dados populados")
    void deveRetornarKPIsComDadosPopulados() throws Exception {
        // ✅ CORREÇÃO: Atualizar viveiro para OCUPADO quando tem lote ativo
        viveiro.setStatus(StatusViveiroEnum.OCUPADO);
        viveiroRepository.save(viveiro);

        // Criar lote com viveiro válido
        Lote lote1 = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        loteRepository.save(lote1);

        mockMvc.perform(get("/v1/dashboard/kpis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(1))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(1));  // ✅ AGORA VAI SER 1
    }
}
