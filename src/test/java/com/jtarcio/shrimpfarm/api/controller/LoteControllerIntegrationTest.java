package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes de Integração - LoteController")
class LoteControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private FazendaRepository fazendaRepository;

    private Fazenda fazenda;
    private Viveiro viveiro;

    @BeforeEach
    void setUp() {
        loteRepository.deleteAll();
        viveiroRepository.deleteAll();
        fazendaRepository.deleteAll();

        // Criar fazenda completa
        fazenda = Fazenda.builder()
                .nome("Fazenda Integração")
                .proprietario("João Teste")
                .endereco("Rua X, 123")
                .cidade("Natal")
                .estado("RN")
                .cep("59000-000")
                .areaTotal(new BigDecimal("10.00"))
                .areaUtil(new BigDecimal("5.00"))
                .telefone("84999990000")
                .email("fazenda@teste.com")
                .ativa(true)
                .build();
        fazenda = fazendaRepository.save(fazenda);

        viveiro = Viveiro.builder()
                .fazenda(fazenda)
                .codigo("V01")
                .nome("Viveiro 01")
                .area(new BigDecimal("1.50"))
                .profundidadeMedia(new BigDecimal("1.20"))
                .volume(new BigDecimal("18000.00"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .ativo(true)
                .build();
        viveiro = viveiroRepository.save(viveiro);
    }

    @Test
    @DisplayName("Deve criar um novo lote com sucesso")
    void deveCriarLoteComSucesso() throws Exception {
        LoteRequest request = LoteRequest.builder()
                .viveiroId(viveiro.getId())
                .codigo("L001")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(50000)
                .custoPosLarvas(new BigDecimal("5000.00"))
                .densidadeInicial(new BigDecimal("30.00"))
                .status(StatusLoteEnum.PLANEJADO)
                .observacoes("Lote teste integração")
                .build();

        String responseJson = mockMvc.perform(post("/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigo").value("L001"))
                .andExpect(jsonPath("$.viveiroId").value(viveiro.getId()))
                .andExpect(jsonPath("$.quantidadePosLarvas").value(50000))
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoteResponse response = objectMapper.readValue(responseJson, LoteResponse.class);
        assertThat(loteRepository.findById(response.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve retornar 404 ao criar lote com viveiro inexistente")
    void deveRetornar404QuandoViveiroNaoExiste() throws Exception {
        LoteRequest request = LoteRequest.builder()
                .viveiroId(999L)
                .codigo("L002")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(10000)
                .custoPosLarvas(new BigDecimal("1000.00"))
                .densidadeInicial(new BigDecimal("20.00"))
                .build();

        mockMvc.perform(post("/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar lote")
    void deveValidarCamposObrigatorios() throws Exception {
        LoteRequest request = LoteRequest.builder().build();

        mockMvc.perform(post("/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
