package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.FornecedorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FornecedorResponse;
import com.jtarcio.shrimpfarm.application.service.FornecedorService;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FornecedorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FornecedorService fornecedorService;

    @Autowired
    private ObjectMapper objectMapper;

    private FornecedorRequest criarRequestValido() {
        return FornecedorRequest.builder()
                .nome("Fornecedor Teste")
                .cnpj("12345678901234")
                .contato("Ana - (11) 88888-8888")
                .endereco("Avenida Principal, 999")
                .ativo(true)
                .build();
    }

    private FornecedorResponse criarResponseValido() {
        return FornecedorResponse.builder()
                .id(1L)
                .nome("Fornecedor Teste")
                .cnpj("12345678901234")
                .contato("Ana - (11) 88888-8888")
                .endereco("Avenida Principal, 999")
                .ativo(true)
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar fornecedor e retornar 201 com corpo")
    void deveCriarFornecedor() throws Exception {
        FornecedorRequest request = criarRequestValido();
        FornecedorResponse response = criarResponseValido();
        when(fornecedorService.criar(any(FornecedorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.nome").value(response.getNome()));
    }

    @Test
    @DisplayName("Deve atualizar fornecedor e retornar 200 com corpo")
    void deveAtualizarFornecedor() throws Exception {
        FornecedorRequest request = criarRequestValido();
        FornecedorResponse response = criarResponseValido();
        when(fornecedorService.atualizar(eq(1L), any(FornecedorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/fornecedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar fornecedor por id e retornar 200")
    void deveBuscarPorId() throws Exception {
        FornecedorResponse response = criarResponseValido();
        when(fornecedorService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/fornecedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 se fornecedor não encontrado")
    void deveRetornar404NaoEncontrado() throws Exception {
        when(fornecedorService.buscarPorId(2L)).thenThrow(new EntityNotFoundException("Fornecedor", 2L));

        mockMvc.perform(get("/v1/fornecedores/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos fornecedores e retornar 200")
    void deveListarTodos() throws Exception {
        FornecedorResponse response = criarResponseValido();
        when(fornecedorService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/fornecedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar fornecedores ativos e retornar 200")
    void deveListarAtivos() throws Exception {
        FornecedorResponse response = criarResponseValido();
        when(fornecedorService.listarAtivos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/fornecedores/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    @DisplayName("Deve listar fornecedores paginados e retornar 200")
    void deveListarPaginado() throws Exception {
        FornecedorResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(fornecedorService.listarPaginado(any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/v1/fornecedores/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve deletar fornecedor e retornar 204")
    void deveDeletarFornecedor() throws Exception {
        doNothing().when(fornecedorService).deletar(1L);

        mockMvc.perform(delete("/v1/fornecedores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve inativar fornecedor e retornar 204")
    void deveInativarFornecedor() throws Exception {
        doNothing().when(fornecedorService).inativar(1L);

        mockMvc.perform(patch("/v1/fornecedores/1/inativar"))
                .andExpect(status().isNoContent());
    }
}
