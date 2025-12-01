package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.application.service.RacaoService;
import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
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

@WebMvcTest(RacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RacaoService racaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private RacaoRequest criarRequestValido() {
        return RacaoRequest.builder()
                .loteId(1L)
                .fornecedorId(2L)
                .dataAplicacao(LocalDate.of(2025, 1, 15))
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Ração Premium")
                .quantidade(BigDecimal.valueOf(100.500))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(BigDecimal.valueOf(12.50))
                .proteinaPercentual(BigDecimal.valueOf(35.0))
                .observacoes("Aplicação diurna")
                .build();
    }

    private RacaoResponse criarResponseValido() {
        return RacaoResponse.builder()
                .id(1L)
                .loteId(1L)
                .loteCodigo("L001")
                .fornecedorId(2L)
                .fornecedorNome("Fornecedor Rações")
                .dataAplicacao(LocalDate.of(2025, 1, 15))
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Ração Premium")
                .quantidade(BigDecimal.valueOf(100.500))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(BigDecimal.valueOf(12.50))
                .custoTotal(BigDecimal.valueOf(1256.25))
                .proteinaPercentual(BigDecimal.valueOf(35.0))
                .observacoes("Aplicação diurna")
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar ração e retornar 201 com corpo")
    void deveCriarRacao() throws Exception {
        RacaoRequest request = criarRequestValido();
        RacaoResponse response = criarResponseValido();
        when(racaoService.criar(any(RacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/racoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.marca").value(response.getMarca()));
    }

    @Test
    @DisplayName("Deve atualizar ração e retornar 200 com corpo")
    void deveAtualizarRacao() throws Exception {
        RacaoRequest request = criarRequestValido();
        RacaoResponse response = criarResponseValido();
        when(racaoService.atualizar(eq(1L), any(RacaoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/racoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar ração por id e retornar 200")
    void deveBuscarPorId() throws Exception {
        RacaoResponse response = criarResponseValido();
        when(racaoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/racoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 se ração não encontrada")
    void deveRetornar404NaoEncontrada() throws Exception {
        when(racaoService.buscarPorId(999L)).thenThrow(new EntityNotFoundException("Racao", 999L));

        mockMvc.perform(get("/v1/racoes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar rações por lote e retornar 200")
    void deveListarPorLote() throws Exception {
        RacaoResponse response = criarResponseValido();
        when(racaoService.listarPorLote(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/racoes/lote/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loteId").value(1L));
    }

    @Test
    @DisplayName("Deve calcular total de ração por lote e retornar 200")
    void deveCalcularTotalPorLote() throws Exception {
        when(racaoService.calcularTotalPorLote(1L)).thenReturn(BigDecimal.valueOf(500.75));

        mockMvc.perform(get("/v1/racoes/lote/1/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500.75));
    }

    @Test
    @DisplayName("Deve listar rações paginadas e retornar 200")
    void deveListarPaginado() throws Exception {
        RacaoResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(racaoService.listarPaginado(any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        // URL SEM parametros - o Pageable é injetado automaticamente com valores default
        mockMvc.perform(get("/v1/racoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve deletar ração e retornar 204")
    void deveDeletarRacao() throws Exception {
        doNothing().when(racaoService).deletar(1L);

        mockMvc.perform(delete("/v1/racoes/1"))
                .andExpect(status().isNoContent());
    }
}
