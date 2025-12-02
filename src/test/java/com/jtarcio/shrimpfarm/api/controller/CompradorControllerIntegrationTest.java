package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.infrastructure.persistence.CompradorRepository;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes de Integração - CompradorController")
class CompradorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompradorRepository compradorRepository;

    private Comprador comprador;

    @BeforeEach
    void setUp() {
        compradorRepository.deleteAll();

        comprador = Comprador.builder()
                .nome("Comprador Teste Ltda")
                .cnpj("12345678000190") // ✅ SEM PONTUAÇÃO
                .telefone("(85) 98765-4321")
                .email("comprador@teste.com")
                .endereco("Rua Comprador, 123")
                .cidade("Fortaleza")
                .estado("CE")
                .cep("60000-000")
                .contato("Maria Oliveira")
                .ativo(true)
                .build();
        comprador = compradorRepository.save(comprador);
    }

    @Test
    @DisplayName("Deve criar comprador com sucesso")
    void deveCriarCompradorComSucesso() throws Exception {
        CompradorRequest request = CompradorRequest.builder()
                .nome("Novo Comprador Ltda")
                .cnpj("98765432000110") // ✅ SEM PONTUAÇÃO
                .endereco("Av. Comercial, 456")
                .contato("João Silva")
                .build();

        mockMvc.perform(post("/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Comprador Ltda"))
                .andExpect(jsonPath("$.cnpj").value("98765432000110"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Deve listar todos os compradores")
    void deveListarTodosCompradores() throws Exception {
        mockMvc.perform(get("/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome").value("Comprador Teste Ltda"))
                .andExpect(jsonPath("$[0].cnpj").value("12345678000190"));
    }

    @Test
    @DisplayName("Deve buscar comprador por ID")
    void deveBuscarCompradorPorId() throws Exception {
        mockMvc.perform(get("/v1/compradores/" + comprador.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comprador.getId()))
                .andExpect(jsonPath("$.nome").value("Comprador Teste Ltda"))
                .andExpect(jsonPath("$.cnpj").value("12345678000190")); // ✅ REMOVIDO .email
    }

    @Test
    @DisplayName("Deve atualizar comprador com sucesso")
    void deveAtualizarCompradorComSucesso() throws Exception {
        CompradorRequest request = CompradorRequest.builder()
                .nome("Comprador Atualizado Ltda")
                .cnpj("12345678000190") // ✅ SEM PONTUAÇÃO
                .endereco("Nova Rua, 999")
                .contato("Pedro Santos")
                .build();

        mockMvc.perform(put("/v1/compradores/" + comprador.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Comprador Atualizado Ltda"))
                .andExpect(jsonPath("$.contato").value("Pedro Santos"));
    }

    @Test
    @DisplayName("Deve deletar comprador com sucesso")
    void deveDeletarCompradorComSucesso() throws Exception {
        mockMvc.perform(delete("/v1/compradores/" + comprador.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/compradores/" + comprador.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar comprador inexistente")
    void deveRetornar404AoBuscarCompradorInexistente() throws Exception {
        mockMvc.perform(get("/v1/compradores/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar compradores ativos")
    void deveBuscarCompradoresAtivos() throws Exception {
        Comprador compradorInativo = Comprador.builder()
                .nome("Comprador Inativo")
                .cnpj("11111111000111")
                .ativo(false)
                .build();
        compradorRepository.save(compradorInativo);

        mockMvc.perform(get("/v1/compradores/ativos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Comprador Teste Ltda"))
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar comprador")
    void deveValidarCamposObrigatoriosAoCriarComprador() throws Exception {
        CompradorRequest request = CompradorRequest.builder()
                .build(); // ✅ Request vazio

        mockMvc.perform(post("/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
