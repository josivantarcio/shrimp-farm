package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Racao;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.RacaoRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - RelatorioController")
class RelatorioControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private FazendaRepository fazendaRepository;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private RacaoRepository racaoRepository;

    private Fazenda fazenda;
    private Viveiro viveiro;
    private Lote lote;

    @BeforeEach
    void setUp() {
        racaoRepository.deleteAll();
        loteRepository.deleteAll();
        viveiroRepository.deleteAll();
        fazendaRepository.deleteAll();

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
                .status(StatusViveiroEnum.OCUPADO)
                .ativo(true)
                .build();
        viveiro = viveiroRepository.save(viveiro);
    }

    @Test
    @DisplayName("Deve retornar KPIs do dashboard vazios")
    void deveRetornarKPIsDashboardVazios() throws Exception {
        mockMvc.perform(get("/v1/relatorios/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(0))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(0));
    }

    @Test
    @DisplayName("Deve retornar KPIs do dashboard com dados")
    void deveRetornarKPIsDashboardComDados() throws Exception {
        lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        loteRepository.save(lote);

        mockMvc.perform(get("/v1/relatorios/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLotesAtivos").value(1))
                .andExpect(jsonPath("$.totalViveirosOcupados").value(1));
    }

    @Test
    @DisplayName("Deve gerar relatório de custo de lote sem rações")
    void deveGerarRelatorioCustoLoteSemRacoes() throws Exception {
        lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        lote = loteRepository.save(lote);

        mockMvc.perform(get("/v1/relatorios/lotes/{loteId}/custos", lote.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loteId").value(lote.getId()))
                .andExpect(jsonPath("$.custoRacao").value(0.0));
    }

    @Test
    @DisplayName("Deve gerar relatório de custo de lote com rações")
    void deveGerarRelatorioCustoLoteComRacoes() throws Exception {
        lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        lote = loteRepository.save(lote);

        // Ração 1: 10 kg a R$ 1,00 = R$ 10,00
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.0"))
                .custoUnitario(new BigDecimal("1.00"))
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .build());

        // Ração 2: 20,5 kg a R$ 1,00 = R$ 20,50
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("20.5"))
                .custoUnitario(new BigDecimal("1.00"))
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .build());

        mockMvc.perform(get("/v1/relatorios/lotes/{loteId}/custos", lote.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loteId").value(lote.getId()))
                // 10,00 + 20,50 = 30,50 de custo de ração
                .andExpect(jsonPath("$.custoRacao").value(30.50));
    }

    @Test
    @DisplayName("Deve listar relatórios de lotes ativos vazios")
    void deveListarRelatoriosLotesAtivosVazios() throws Exception {
        mockMvc.perform(get("/v1/relatorios/lotes/ativos/custos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve listar relatórios de lotes ativos com dados")
    void deveListarRelatoriosLotesAtivosComDados() throws Exception {
        lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        loteRepository.save(lote);

        Lote loteInativo = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE002")
                .dataPovoamento(LocalDate.now().minusDays(60))
                .quantidadePosLarvas(30000)
                .status(StatusLoteEnum.FINALIZADO)
                .build();
        loteRepository.save(loteInativo);

        mockMvc.perform(get("/v1/relatorios/lotes/ativos/custos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].loteId").exists());
    }
}
