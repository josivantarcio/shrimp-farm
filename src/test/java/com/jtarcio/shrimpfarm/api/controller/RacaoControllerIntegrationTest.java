package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - RacaoController")
class RacaoControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RacaoRepository racaoRepository;

    @Autowired
    private FazendaRepository fazendaRepository;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private LoteRepository loteRepository;

    private Fazenda fazenda;
    private Viveiro viveiro;
    private Lote lote;
    private Racao racao;

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

        lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE001")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(50000)
                .status(StatusLoteEnum.ATIVO)
                .build();
        lote = loteRepository.save(lote);
    }

    @Test
    @DisplayName("Deve criar uma nova ração com sucesso")
    void deveCriarRacaoComSucesso() throws Exception {
        RacaoRequest request = RacaoRequest.builder()
                .loteId(lote.getId())
                .dataAplicacao(LocalDate.now())  // ← Data atual
                .quantidade(new BigDecimal("10.50"))  // ← CAMPO CORRETO
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Teste ração")
                .build();

        mockMvc.perform(post("/v1/racoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantidade").value(10.50))  // ← CORRIGIDO
                .andExpect(jsonPath("$.loteId").value(lote.getId()));
    }

    @Test
    @DisplayName("Deve buscar ração por ID")
    void deveBuscarRacaoPorId() throws Exception {
        racao = racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.50"))  // ← CAMPO CORRETO
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Teste ração")
                .build());

        mockMvc.perform(get("/v1/racoes/{id}", racao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(racao.getId()))
                .andExpect(jsonPath("$.quantidade").value(10.50));  // ← CORRIGIDO
    }

    @Test
    @DisplayName("Deve listar rações por lote")
    void deveListarRacoesPorLote() throws Exception {
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.0"))
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Ração 1")
                .build());
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now().minusDays(1))
                .quantidade(new BigDecimal("15.0"))
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Guabi")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Ração 2")
                .build());

        mockMvc.perform(get("/v1/racoes/lote/{loteId}", lote.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].loteId").value(lote.getId()));
    }

    @Test
    @DisplayName("Deve calcular total de ração por lote")
    void deveCalcularTotalRacaoPorLote() throws Exception {
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.0"))  // ← CAMPO CORRETO
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .build());
        racaoRepository.save(Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("20.5"))
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Guabi")
                .unidade(UnidadeMedidaEnum.KG)
                .build());

        mockMvc.perform(get("/v1/racoes/lote/{loteId}/total", lote.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(30.5));
    }

    @Test
    @DisplayName("Deve listar rações paginado")
    void deveListarRacoesPaginado() throws Exception {
        mockMvc.perform(get("/v1/racoes")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Deve atualizar ração com sucesso")
    void deveAtualizarRacaoComSucesso() throws Exception {
        Racao racaoExistente = Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("5.0"))
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Antiga")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Antiga")
                .build();
        racao = racaoRepository.save(racaoExistente);

        RacaoRequest request = RacaoRequest.builder()
                .loteId(lote.getId())
                .dataAplicacao(LocalDate.now())  // ← Data atual (não futura)
                .quantidade(new BigDecimal("25.0"))  // ← CAMPO CORRETO
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Nova")
                .unidade(UnidadeMedidaEnum.KG)
                .observacoes("Atualizada")
                .build();

        mockMvc.perform(put("/v1/racoes/{id}", racao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(racao.getId()))
                .andExpect(jsonPath("$.quantidade").value(25.0));  // ← CORRIGIDO
    }


    @Test
    @DisplayName("Deve deletar ração com sucesso")
    void deveDeletarRacaoComSucesso() throws Exception {
        Racao racaoExistente = Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .quantidade(new BigDecimal("10.0"))
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Potimar")
                .unidade(UnidadeMedidaEnum.KG)
                .build();
        racao = racaoRepository.save(racaoExistente);

        mockMvc.perform(delete("/v1/racoes/{id}", racao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/racoes/{id}", racao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar ração")
    void deveValidarCamposObrigatoriosAoCriarRacao() throws Exception {
        RacaoRequest request = RacaoRequest.builder().build();

        mockMvc.perform(post("/v1/racoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}
