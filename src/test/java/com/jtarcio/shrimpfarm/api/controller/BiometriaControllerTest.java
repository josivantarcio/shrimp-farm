package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.application.service.BiometriaService;
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

@WebMvcTest(BiometriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BiometriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BiometriaService biometriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private BiometriaRequest criarRequestValido() {
        return BiometriaRequest.builder()
                .loteId(1L)
                .dataBiometria(LocalDate.of(2025, 1, 10))
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(30)
                .pesoTotalAmostra(BigDecimal.valueOf(0.465))
                .observacoes("Amostra teste")
                .build();
    }

    private BiometriaResponse criarResponseValido() {
        return BiometriaResponse.builder()
                .id(1L)
                .loteId(1L)
                .loteCodigo("L001")
                .dataBiometria(LocalDate.of(2025, 1, 10))
                .diaCultivo(12)
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(30)
                .pesoTotalAmostra(BigDecimal.valueOf(0.465))
                .ganhoPesoDiario(BigDecimal.valueOf(0.5))
                .biomassaEstimada(BigDecimal.valueOf(450.0))
                .sobrevivenciaEstimada(BigDecimal.valueOf(93.0))
                .fatorConversaoAlimentar(BigDecimal.valueOf(1.2))
                .observacoes("Amostra teste")
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar biometria e retornar 201 com corpo")
    void deveCriarBiometria() throws Exception {
        BiometriaRequest request = criarRequestValido();
        BiometriaResponse response = criarResponseValido();
        when(biometriaService.criar(any(BiometriaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/biometrias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve atualizar biometria e retornar 200 com corpo")
    void deveAtualizarBiometria() throws Exception {
        BiometriaRequest request = criarRequestValido();
        BiometriaResponse response = criarResponseValido();
        when(biometriaService.atualizar(eq(1L), any(BiometriaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/biometrias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar biometria por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        BiometriaResponse response = criarResponseValido();
        when(biometriaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/biometrias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 se biometria não encontrada")
    void deveRetornar404NaoEncontrado() throws Exception {
        when(biometriaService.buscarPorId(2L)).thenThrow(new EntityNotFoundException("Biometria", 2L));

        mockMvc.perform(get("/v1/biometrias/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar biometrias por lote e retornar 200")
    void deveListarPorLote() throws Exception {
        BiometriaResponse response = criarResponseValido();
        when(biometriaService.listarPorLote(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/biometrias/lote/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loteId").value(1L));
    }

    @Test
    @DisplayName("Deve buscar última biometria do lote e retornar 200")
    void deveBuscarUltimaBiometriaDoLote() throws Exception {
        BiometriaResponse response = criarResponseValido();
        when(biometriaService.buscarUltimaBiometriaDoLote(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/biometrias/lote/1/ultima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar biometrias paginadas e retornar 200")
    void deveListarPaginado() throws Exception {
        BiometriaResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(biometriaService.listarPaginado(any())).thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/v1/biometrias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve deletar biometria e retornar 204")
    void deveDeletarBiometria() throws Exception {
        doNothing().when(biometriaService).deletar(1L);

        mockMvc.perform(delete("/v1/biometrias/1"))
                .andExpect(status().isNoContent());
    }
}
