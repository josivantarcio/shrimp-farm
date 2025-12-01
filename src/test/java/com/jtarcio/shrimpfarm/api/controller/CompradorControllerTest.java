package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.application.service.CompradorService;
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

@WebMvcTest(CompradorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CompradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompradorService compradorService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompradorRequest criarRequestValido() {
        return CompradorRequest.builder()
                .nome("Comprador Teste")
                .cnpj("12345678901234")
                .contato("João - (11) 99999-9999")
                .endereco("Rua X, 123")
                .ativo(true)
                .build();
    }

    private CompradorResponse criarResponseValido() {
        return CompradorResponse.builder()
                .id(1L)
                .nome("Comprador Teste")
                .cnpj("12345678901234")
                .contato("João - (11) 99999-9999")
                .endereco("Rua X, 123")
                .ativo(true)
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar comprador e retornar 201 com corpo")
    void deveCriarComprador() throws Exception {
        CompradorRequest request = criarRequestValido();
        CompradorResponse response = criarResponseValido();
        when(compradorService.criar(any(CompradorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.nome").value(response.getNome()));
    }

    @Test
    @DisplayName("Deve atualizar comprador e retornar 200 com corpo")
    void deveAtualizarComprador() throws Exception {
        CompradorRequest request = criarRequestValido();
        CompradorResponse response = criarResponseValido();
        when(compradorService.atualizar(eq(1L), any(CompradorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/compradores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar comprador por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        CompradorResponse response = criarResponseValido();
        when(compradorService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/compradores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value(response.getNome()));
    }

    @Test
    @DisplayName("Deve retornar 404 se comprador não encontrado")
    void deveRetornar404NaoEncontrado() throws Exception {
        when(compradorService.buscarPorId(2L)).thenThrow(new EntityNotFoundException("Comprador", 2L));

        mockMvc.perform(get("/v1/compradores/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar compradores paginados e retornar 200")
    void deveListarPaginado() throws Exception {
        CompradorResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(compradorService.listarPaginado(any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/v1/compradores/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar todos compradores e retornar 200")
    void deveListarTodos() throws Exception {
        CompradorResponse response = criarResponseValido();
        when(compradorService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/compradores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar compradores ativos e retornar 200")
    void deveListarAtivos() throws Exception {
        CompradorResponse response = criarResponseValido();
        when(compradorService.listarAtivos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/compradores/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    @DisplayName("Deve inativar comprador e retornar 204")
    void deveInativarComprador() throws Exception {
        doNothing().when(compradorService).inativar(1L);

        mockMvc.perform(patch("/v1/compradores/1/inativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve deletar comprador e retornar 204")
    void deveDeletarComprador() throws Exception {
        doNothing().when(compradorService).deletar(1L);

        mockMvc.perform(delete("/v1/compradores/1"))
                .andExpect(status().isNoContent());
    }
}
