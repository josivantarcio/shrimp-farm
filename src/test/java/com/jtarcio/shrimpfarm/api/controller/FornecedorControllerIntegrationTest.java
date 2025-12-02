package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FornecedorRepository;
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
@DisplayName("Testes de Integração - FornecedorController")
class FornecedorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        fornecedorRepository.deleteAll();

        // Criar fornecedor
        fornecedor = Fornecedor.builder()
                .nome("Fornecedor Teste")
                .cnpj("12.345.678/0001-90")
                .telefone("(85) 98765-4321")
                .email("fornecedor@teste.com")
                .endereco("Rua Teste, 123")
                .cidade("Fortaleza")
                .estado("CE")
                .cep("60000-000")
                .contato("João Silva")
                .ativo(true)
                .build();
        fornecedor = fornecedorRepository.save(fornecedor);
    }

    @Test
    @DisplayName("Deve criar fornecedor com sucesso")
    void deveCriarFornecedorComSucesso() throws Exception {
        FornecedorRequest request = new FornecedorRequest();
        request.setNome("Novo Fornecedor");
        request.setCnpj("98.765.432/0001-10");
        request.setTelefone("(85) 91234-5678");
        request.setEmail("novo@fornecedor.com");
        request.setEndereco("Av. Principal, 456");
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setCep("01000-000");
        request.setContato("Maria Santos");

        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Fornecedor"))
                .andExpect(jsonPath("$.cnpj").value("98.765.432/0001-10"))
                .andExpect(jsonPath("$.email").value("novo@fornecedor.com"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Deve listar todos os fornecedores")
    void deveListarTodosFornecedores() throws Exception {
        mockMvc.perform(get("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome").value("Fornecedor Teste"))
                .andExpect(jsonPath("$[0].cnpj").value("12.345.678/0001-90"));
    }

    @Test
    @DisplayName("Deve buscar fornecedor por ID")
    void deveBuscarFornecedorPorId() throws Exception {
        mockMvc.perform(get("/v1/fornecedores/" + fornecedor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fornecedor.getId()))
                .andExpect(jsonPath("$.nome").value("Fornecedor Teste"))
                .andExpect(jsonPath("$.email").value("fornecedor@teste.com"))
                .andExpect(jsonPath("$.telefone").value("(85) 98765-4321"));
    }

    @Test
    @DisplayName("Deve atualizar fornecedor com sucesso")
    void deveAtualizarFornecedorComSucesso() throws Exception {
        FornecedorRequest request = new FornecedorRequest();
        request.setNome("Fornecedor Atualizado");
        request.setCnpj("12.345.678/0001-90");
        request.setTelefone("(85) 99999-9999");
        request.setEmail("atualizado@fornecedor.com");
        request.setEndereco("Nova Rua, 789");
        request.setCidade("Fortaleza");
        request.setEstado("CE");
        request.setCep("60100-000");
        request.setContato("José Santos");

        mockMvc.perform(put("/v1/fornecedores/" + fornecedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fornecedor Atualizado"))
                .andExpect(jsonPath("$.telefone").value("(85) 99999-9999"))
                .andExpect(jsonPath("$.email").value("atualizado@fornecedor.com"));
    }

    @Test
    @DisplayName("Deve deletar fornecedor com sucesso")
    void deveDeletarFornecedorComSucesso() throws Exception {
        mockMvc.perform(delete("/v1/fornecedores/" + fornecedor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar que foi deletado
        mockMvc.perform(get("/v1/fornecedores/" + fornecedor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar fornecedor inexistente")
    void deveRetornar404AoBuscarFornecedorInexistente() throws Exception {
        mockMvc.perform(get("/v1/fornecedores/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar fornecedores ativos")
    void deveBuscarFornecedoresAtivos() throws Exception {
        // Criar fornecedor inativo
        Fornecedor fornecedorInativo = Fornecedor.builder()
                .nome("Fornecedor Inativo")
                .cnpj("11.111.111/0001-11")
                .ativo(false)
                .build();
        fornecedorRepository.save(fornecedorInativo);

        mockMvc.perform(get("/v1/fornecedores/ativos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Fornecedor Teste"))
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar fornecedor")
    void deveValidarCamposObrigatoriosAoCriarFornecedor() throws Exception {
        FornecedorRequest request = new FornecedorRequest();
        // Request vazio - campos obrigatórios faltando

        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
