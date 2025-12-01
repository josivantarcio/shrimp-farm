package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.application.service.ViveiroService;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViveiroController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ViveiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ViveiroService viveiroService;

    @Autowired
    private ObjectMapper objectMapper;

    private ViveiroRequest criarRequestValido() {
        return ViveiroRequest.builder()
                .fazendaId(1L)
                .codigo("V001")
                .nome("Viveiro Teste")
                .area(BigDecimal.valueOf(100.0))
                .profundidadeMedia(BigDecimal.valueOf(1.5))
                .volume(BigDecimal.valueOf(150.0))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Teste de viveiro")
                .ativo(true)
                .build();
    }

    private ViveiroResponse criarResponseValido() {
        return ViveiroResponse.builder()
                .id(1L)
                .fazendaId(1L)
                .fazendaNome("Fazenda X")
                .codigo("V001")
                .nome("Viveiro Teste")
                .area(BigDecimal.valueOf(100.0))
                .profundidadeMedia(BigDecimal.valueOf(1.5))
                .volume(BigDecimal.valueOf(150.0))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Teste de viveiro")
                .ativo(true)
                .quantidadeLotes(3)
                .dataCriacao(null)
                .dataAtualizacao(null)
                .build();
    }

    @Test
    @DisplayName("Deve criar viveiro e retornar 201 com corpo")
    void deveCriarViveiro() throws Exception {
        ViveiroRequest request = criarRequestValido();
        ViveiroResponse response = criarResponseValido();
        when(viveiroService.criar(any(ViveiroRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/viveiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.codigo").value(response.getCodigo()));
    }

    @Test
    @DisplayName("Deve atualizar viveiro e retornar 200 com corpo")
    void deveAtualizarViveiro() throws Exception {
        ViveiroRequest request = criarRequestValido();
        ViveiroResponse response = criarResponseValido();
        when(viveiroService.atualizar(eq(1L), any(ViveiroRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/viveiros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve buscar viveiro por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        ViveiroResponse response = criarResponseValido();
        when(viveiroService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/viveiros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value(response.getCodigo()));
    }

    @Test
    @DisplayName("Deve retornar 404 se viveiro não encontrado")
    void deveRetornar404NaoEncontrado() throws Exception {
        when(viveiroService.buscarPorId(2L)).thenThrow(new EntityNotFoundException("Viveiro", 2L));

        mockMvc.perform(get("/v1/viveiros/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos viveiros e retornar 200")
    void deveListarTodos() throws Exception {
        ViveiroResponse response = criarResponseValido();
        when(viveiroService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/viveiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve listar viveiros por fazenda e retornar 200")
    void deveListarPorFazenda() throws Exception {
        ViveiroResponse response = criarResponseValido();
        when(viveiroService.listarPorFazenda(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/viveiros/fazenda/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fazendaId").value(1L));
    }

    @Test
    @DisplayName("Deve listar viveiros paginados e retornar 200")
    void deveListarPaginado() throws Exception {
        ViveiroResponse response = criarResponseValido();
        PageRequest pageable = PageRequest.of(0, 10);
        when(viveiroService.listarPaginado(any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/v1/viveiros/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve deletar viveiro e retornar 204")
    void deveDeletarViveiro() throws Exception {
        doNothing().when(viveiroService).deletar(1L);

        mockMvc.perform(delete("/v1/viveiros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve inativar viveiro e retornar 204")
    void deveInativarViveiro() throws Exception {
        doNothing().when(viveiroService).inativar(1L);

        mockMvc.perform(patch("/v1/viveiros/1/inativar"))
                .andExpect(status().isNoContent());
    }
}
