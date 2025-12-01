package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.application.service.DespescaService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DespescaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DespescaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DespescaService despescaService;

    @Autowired
    private ObjectMapper objectMapper;

    private DespescaRequest criarRequestValido() {
        return DespescaRequest.builder()
                .loteId(1L)
                .compradorId(2L)
                .dataDespesca(LocalDate.of(2025, 1, 20))
                .pesoTotal(BigDecimal.valueOf(1500.50))
                .quantidadeDespescada(100_000)
                .pesoMedioFinal(BigDecimal.valueOf(15.5))
                .precoVendaKg(BigDecimal.valueOf(25.30))
                .custoDespesca(BigDecimal.valueOf(1200.00))
                .observacoes("Primeira despesca do lote")
                .build();
    }

    private DespescaResponse criarResponseValido() {
        return DespescaResponse.builder()
                .id(1L)
                .loteId(1L)
                .loteCodigo("L001")
                .compradorId(2L)
                .compradorNome("Comprador Teste")
                .dataDespesca(LocalDate.of(2025, 1, 20))
                .pesoTotal(BigDecimal.valueOf(1500.50))
                .quantidadeDespescada(100_000)
                .pesoMedioFinal(BigDecimal.valueOf(15.5))
                .taxaSobrevivencia(BigDecimal.valueOf(85.0))
                .precoVendaKg(BigDecimal.valueOf(25.30))
                .receitaTotal(BigDecimal.valueOf(37937.65))
                .custoDespesca(BigDecimal.valueOf(1200.00))
                .observacoes("Primeira despesca do lote")
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar despesca e retornar 201")
    void deveCriarDespesca() throws Exception {
        DespescaRequest request = criarRequestValido();
        DespescaResponse response = criarResponseValido();
        when(despescaService.criar(any(DespescaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.loteId").value(response.getLoteId()));
    }

    @Test
    @DisplayName("Deve atualizar despesca e retornar 200")
    void deveAtualizarDespesca() throws Exception {
        DespescaRequest request = criarRequestValido();
        DespescaResponse response = criarResponseValido();
        when(despescaService.atualizar(eq(1L), any(DespescaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/despescas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar despesca por id e retornar 200")
    void deveBuscarPorId() throws Exception {
        DespescaResponse response = criarResponseValido();
        when(despescaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/despescas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 se despesca não encontrada")
    void deveRetornar404SeNaoEncontrada() throws Exception {
        when(despescaService.buscarPorId(999L))
                .thenThrow(new EntityNotFoundException("Despesca", 999L));

        mockMvc.perform(get("/v1/despescas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar despescas por lote e retornar 200")
    void deveListarPorLote() throws Exception {
        DespescaResponse response = criarResponseValido();

        // buscarPorLote retorna apenas um DespescaResponse
        when(despescaService.buscarPorLote(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/despescas/lote/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loteId").value(1L));
    }


    @Test
    @DisplayName("Deve deletar despesca e retornar 204")
    void deveDeletarDespesca() throws Exception {
        doNothing().when(despescaService).deletar(1L);

        mockMvc.perform(delete("/v1/despescas/1"))
                .andExpect(status().isNoContent());
    }
}
