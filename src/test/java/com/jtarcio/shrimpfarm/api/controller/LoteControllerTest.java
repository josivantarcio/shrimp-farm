package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.application.service.LoteService;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoteService loteService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoteRequest criarRequestValido() {
        return LoteRequest.builder()
                .viveiroId(1L)
                .codigo("L001")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .dataDespesca(null) // opcional
                .quantidadePosLarvas(10000)
                .custoPosLarvas(BigDecimal.valueOf(500.00))
                .densidadeInicial(BigDecimal.valueOf(20.0))
                .status(StatusLoteEnum.PLANEJADO)
                .observacoes("Lote de teste")
                .build();
    }

    private LoteResponse criarResponseValido() {
        return LoteResponse.builder()
                .id(1L)
                .viveiroId(1L)
                .viveiroCodigo("V001")
                .viveiroNome("Viveiro 1")
                .fazendaId(1L)
                .fazendaNome("Fazenda Teste")
                .codigo("L001")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .dataDespesca(null)
                .quantidadePosLarvas(10000)
                .custoPosLarvas(BigDecimal.valueOf(500.00))
                .densidadeInicial(BigDecimal.valueOf(20.0))
                .status(StatusLoteEnum.PLANEJADO)
                .diasCultivo(0)
                .observacoes("Lote de teste")
                .quantidadeBiometrias(0)
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar lote e retornar 201 com corpo")
    void deveCriarLote() throws Exception {
        LoteRequest request = criarRequestValido();
        LoteResponse response = criarResponseValido();
        when(loteService.criar(any(LoteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.codigo").value(response.getCodigo()));
    }

    @Test
    @DisplayName("Deve atualizar lote e retornar 200 com corpo")
    void deveAtualizarLote() throws Exception {
        LoteRequest request = criarRequestValido();
        LoteResponse response = criarResponseValido();
        when(loteService.atualizar(eq(1L), any(LoteRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/lotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar lote por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        LoteResponse response = criarResponseValido();
        when(loteService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/lotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value(response.getCodigo()));
    }

    @Test
    @DisplayName("Deve retornar 404 se lote não encontrado")
    void deveRetornar404NaoEncontrado() throws Exception {
        when(loteService.buscarPorId(2L)).thenThrow(new EntityNotFoundException("Lote", 2L));

        mockMvc.perform(get("/v1/lotes/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos lotes e retornar 200")
    void deveListarTodos() throws Exception {
        LoteResponse response = criarResponseValido();
        when(loteService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar lotes ativos e retornar 200")
    void deveListarAtivos() throws Exception {
        LoteResponse response = criarResponseValido();
        when(loteService.listarAtivos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/lotes/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(response.getStatus().name()));
    }

    @Test
    @DisplayName("Deve listar lotes por viveiro e retornar 200")
    void deveListarPorViveiro() throws Exception {
        LoteResponse response = criarResponseValido();
        when(loteService.listarPorViveiro(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/lotes/viveiro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].viveiroId").value(1L));
    }

    @Test
    @DisplayName("Deve listar lotes paginados e retornar 200")
    void deveListarPaginado() throws Exception {
        LoteResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(loteService.listarPaginado(any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/v1/lotes/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve deletar lote e retornar 204")
    void deveDeletarLote() throws Exception {
        doNothing().when(loteService).deletar(1L);

        mockMvc.perform(delete("/v1/lotes/1"))
                .andExpect(status().isNoContent());
    }
}
