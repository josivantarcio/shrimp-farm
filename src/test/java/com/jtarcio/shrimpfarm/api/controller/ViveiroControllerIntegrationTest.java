package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes de Integração - ViveiroController")
class ViveiroControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private FazendaRepository fazendaRepository;

    private Fazenda fazenda;
    private Viveiro viveiro;

    @BeforeEach
    void setUp() {
        viveiroRepository.deleteAll();
        fazendaRepository.deleteAll();

        // Criar fazenda
        fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();
        fazenda = fazendaRepository.save(fazenda);

        // Criar viveiro
        viveiro = Viveiro.builder()
                .nome("Viveiro 1")
                .codigo("V001")
                .area(BigDecimal.valueOf(1000))
                .profundidadeMedia(BigDecimal.valueOf(1.5))
                .status(StatusViveiroEnum.DISPONIVEL)
                .fazenda(fazenda)
                .ativo(true)
                .build();
        viveiro = viveiroRepository.save(viveiro);
    }

    @Test
    @DisplayName("Deve criar viveiro com sucesso")
    void deveCriarViveiroComSucesso() throws Exception {
        ViveiroRequest request = new ViveiroRequest();
        request.setNome("Viveiro 2");
        request.setCodigo("V002");
        request.setArea(BigDecimal.valueOf(1500));
        request.setProfundidadeMedia(BigDecimal.valueOf(2.0));
        request.setFazendaId(fazenda.getId());

        mockMvc.perform(post("/v1/viveiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Viveiro 2"))
                .andExpect(jsonPath("$.codigo").value("V002"))
                .andExpect(jsonPath("$.area").value(1500))
                .andExpect(jsonPath("$.profundidadeMedia").value(2.0))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));
    }

    @Test
    @DisplayName("Deve listar todos os viveiros")
    void deveListarTodosViveiros() throws Exception {
        mockMvc.perform(get("/v1/viveiros")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome").value("Viveiro 1"))
                .andExpect(jsonPath("$[0].codigo").value("V001"));
    }

    @Test
    @DisplayName("Deve buscar viveiro por ID")
    void deveBuscarViveiroPorId() throws Exception {
        mockMvc.perform(get("/v1/viveiros/" + viveiro.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(viveiro.getId()))
                .andExpect(jsonPath("$.nome").value("Viveiro 1"))
                .andExpect(jsonPath("$.codigo").value("V001"))
                .andExpect(jsonPath("$.area").value(1000));
    }

    @Test
    @DisplayName("Deve atualizar viveiro com sucesso")
    void deveAtualizarViveiroComSucesso() throws Exception {
        ViveiroRequest request = new ViveiroRequest();
        request.setNome("Viveiro Atualizado");
        request.setCodigo("V001-UPD");
        request.setArea(BigDecimal.valueOf(1200));
        request.setProfundidadeMedia(BigDecimal.valueOf(1.8));
        request.setFazendaId(fazenda.getId());

        mockMvc.perform(put("/v1/viveiros/" + viveiro.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Viveiro Atualizado"))
                .andExpect(jsonPath("$.codigo").value("V001-UPD"))
                .andExpect(jsonPath("$.area").value(1200));
    }

    @Test
    @DisplayName("Deve deletar viveiro com sucesso")
    void deveDeletarViveiroComSucesso() throws Exception {
        mockMvc.perform(delete("/v1/viveiros/" + viveiro.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar que foi deletado
        mockMvc.perform(get("/v1/viveiros/" + viveiro.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar viveiro inexistente")
    void deveRetornar404AoBuscarViveiroInexistente() throws Exception {
        mockMvc.perform(get("/v1/viveiros/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar viveiros por fazenda")
    void deveBuscarViveirosPorFazenda() throws Exception {
        mockMvc.perform(get("/v1/viveiros/fazenda/" + fazenda.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Viveiro 1"));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar viveiro")
    void deveValidarCamposObrigatoriosAoCriarViveiro() throws Exception {
        ViveiroRequest request = new ViveiroRequest();
        // Request vazio - todos os campos obrigatórios faltando

        mockMvc.perform(post("/v1/viveiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
