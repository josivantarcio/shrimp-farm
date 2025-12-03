package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - FazendaController")
class FazendaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;  // ← ADICIONADO AQUI

    @Autowired
    private FazendaRepository fazendaRepository;

    private Fazenda fazenda;

    @BeforeEach
    void setUp() {
        fazendaRepository.deleteAll();

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
    }

    @Test
    @DisplayName("Deve criar uma nova fazenda com sucesso")
    void deveCriarFazendaComSucesso() throws Exception {
        FazendaRequest request = FazendaRequest.builder()
                .nome("Fazenda Nova")
                .proprietario("Maria Teste")
                .endereco("Rua Y, 456")
                .cidade("Mossoró")
                .estado("RN")
                .cep("59600-000")
                .areaTotal(new BigDecimal("20.00"))
                .areaUtil(new BigDecimal("12.00"))
                .telefone("84988880000")
                .email("nova@fazenda.com")
                .ativa(true)
                .build();

        mockMvc.perform(post("/v1/fazendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Fazenda Nova"))
                .andExpect(jsonPath("$.cidade").value("Mossoró"))
                .andExpect(jsonPath("$.estado").value("RN"))
                .andExpect(jsonPath("$.ativa").value(true));
    }

    @Test
    @DisplayName("Deve buscar fazenda por ID")
    void deveBuscarFazendaPorId() throws Exception {
        mockMvc.perform(get("/v1/fazendas/{id}", fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fazenda.getId()))
                .andExpect(jsonPath("$.nome").value("Fazenda Integração"))
                .andExpect(jsonPath("$.cidade").value("Natal"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar fazenda inexistente")
    void deveRetornar404AoBuscarFazendaInexistente() throws Exception {
        mockMvc.perform(get("/v1/fazendas/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todas as fazendas")
    void deveListarTodasFazendas() throws Exception {
        mockMvc.perform(get("/v1/fazendas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome", notNullValue()));
    }

    @Test
    @DisplayName("Deve listar apenas fazendas ativas")
    void deveListarFazendasAtivas() throws Exception {
        fazenda.setAtiva(false);
        fazendaRepository.save(fazenda);

        mockMvc.perform(get("/v1/fazendas/ativas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve atualizar fazenda com sucesso")
    void deveAtualizarFazendaComSucesso() throws Exception {
        FazendaRequest request = FazendaRequest.builder()
                .nome("Fazenda Atualizada")
                .proprietario("João Atualizado")
                .endereco("Rua Z, 789")
                .cidade("Parnamirim")
                .estado("RN")
                .cep("59140-000")
                .areaTotal(new BigDecimal("15.00"))
                .areaUtil(new BigDecimal("8.00"))
                .telefone("84977770000")
                .email("atualizada@fazenda.com")
                .ativa(true)
                .build();

        mockMvc.perform(put("/v1/fazendas/{id}", fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fazenda.getId()))
                .andExpect(jsonPath("$.nome").value("Fazenda Atualizada"))
                .andExpect(jsonPath("$.cidade").value("Parnamirim"));
    }

    @Test
    @DisplayName("Deve deletar fazenda com sucesso")
    void deveDeletarFazendaComSucesso() throws Exception {
        mockMvc.perform(delete("/v1/fazendas/{id}", fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/fazendas/{id}", fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve inativar fazenda com sucesso")
    void deveInativarFazendaComSucesso() throws Exception {
        mockMvc.perform(patch("/v1/fazendas/{id}/inativar", fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Fazenda inativada = fazendaRepository.findById(fazenda.getId()).orElseThrow();
        assertThat(inativada.getAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar fazenda")
    void deveValidarCamposObrigatoriosAoCriarFazenda() throws Exception {
        FazendaRequest request = FazendaRequest.builder().build();

        mockMvc.perform(post("/v1/fazendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}
