package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.api.controller.FazendaController;
import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.application.service.FazendaService;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FazendaController.class)
@AutoConfigureMockMvc(addFilters = false) // desativa filtros do Spring Security nos testes
@ActiveProfiles("test")
class FazendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FazendaService fazendaService;

    @Autowired
    private ObjectMapper objectMapper;

    private FazendaRequest criarRequestValido() {
        return FazendaRequest.builder()
                .nome("Fazenda Teste")
                .proprietario("João Silva")
                .endereco("Endereço 123")
                .cidade("Cidade X")
                .estado("SP")
                .cep("12345-678")
                .areaTotal(BigDecimal.valueOf(100.0))
                .areaUtil(BigDecimal.valueOf(90.0))
                .telefone("11 99999-9999")
                .email("teste@fazenda.com")
                .observacoes("Observações da fazenda.")
                .ativa(true)
                .build();
    }

    private FazendaResponse criarResponseValido() {
        return FazendaResponse.builder()
                .id(1L)
                .nome("Fazenda Teste")
                .proprietario("João Silva")
                .endereco("Endereço 123")
                .cidade("Cidade X")
                .estado("SP")
                .cep("12345-678")
                .areaTotal(BigDecimal.valueOf(100.0))
                .areaUtil(BigDecimal.valueOf(90.0))
                .telefone("11 99999-9999")
                .email("teste@fazenda.com")
                .observacoes("Observação")
                .ativa(true)
                .quantidadeViveiros(2)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar fazenda e retornar 201 com corpo")
    void deveCriarFazenda() throws Exception {
        FazendaRequest request = criarRequestValido();
        FazendaResponse response = criarResponseValido();
        when(fazendaService.criar(any(FazendaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/fazendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.nome").value(response.getNome()));
    }

    @Test
    @DisplayName("Deve buscar fazenda por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        FazendaResponse response = criarResponseValido();
        when(fazendaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/fazendas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value(response.getNome()));
    }

    @Test
    @DisplayName("Deve retornar 404 se fazenda não encontrada por id")
    void deveRetornar404FazendaNaoEncontrada() throws Exception {
        when(fazendaService.buscarPorId(2L))
                .thenThrow(new EntityNotFoundException("Fazenda", 2L));

        mockMvc.perform(get("/v1/fazendas/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todas as fazendas e retornar 200")
    void deveListarTodas() throws Exception {
        FazendaResponse response = criarResponseValido();
        when(fazendaService.listarTodas()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/v1/fazendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar fazendas paginadas e retornar 200")
    void deveListarPaginado() throws Exception {
        FazendaResponse response = criarResponseValido();
        when(fazendaService.listarPaginado(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(response)));

        mockMvc.perform(get("/v1/fazendas/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve atualizar fazenda e retornar 200 com corpo")
    void deveAtualizarFazenda() throws Exception {
        FazendaRequest request = criarRequestValido();
        FazendaResponse response = criarResponseValido();
        when(fazendaService.atualizar(any(Long.class), any(FazendaRequest.class))).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/v1/fazendas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deveDeletarFazenda() throws Exception {
        doNothing().when(fazendaService).deletar(1L);

        mockMvc.perform(delete("/v1/fazendas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve inativar fazenda e retornar 204")
    void deveInativarFazenda() throws Exception {
        doNothing().when(fazendaService).inativar(1L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/v1/fazendas/1/inativar"))
                .andExpect(status().isNoContent());
    }
}
