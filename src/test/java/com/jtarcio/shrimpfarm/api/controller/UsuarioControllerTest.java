package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.application.service.UsuarioService;
import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioRequest criarRequestValido() {
        return UsuarioRequest.builder()
                .username("usuario_teste")          // <-- CAMPO OBRIGATÓRIO
                .nome("Usuário Teste")
                .email("usuario@teste.com")
                .senha("SenhaForte123")
                .papel(RoleEnum.ADMIN)
                .build();
    }


    private UsuarioResponse criarResponseValido() {
        return UsuarioResponse.builder()
                .id(1L)
                .nome("Usuário Teste")
                .email("usuario@teste.com")
                .papel(RoleEnum.ADMIN)
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar usuário e retornar 201 com corpo")
    void deveCriarUsuario() throws Exception {
        UsuarioRequest request = criarRequestValido();
        UsuarioResponse response = criarResponseValido();
        when(usuarioService.criar(any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.email").value(response.getEmail()));
    }

    @Test
    @DisplayName("Deve buscar usuário por id e retornar 200 com corpo")
    void deveBuscarPorId() throws Exception {
        UsuarioResponse response = criarResponseValido();
        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(response.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar 404 se usuário não encontrado por id")
    void deveRetornar404UsuarioNaoEncontrado() throws Exception {
        when(usuarioService.buscarPorId(2L))
                .thenThrow(new EntityNotFoundException("Usuario", 2L));

        mockMvc.perform(get("/v1/usuarios/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos usuários e retornar 200")
    void deveListarTodos() throws Exception {
        UsuarioResponse response = criarResponseValido();
        when(usuarioService.listarTodos()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(response.getId()));
    }

    @Test
    @DisplayName("Deve atualizar usuário e retornar 200 com corpo")
    void deveAtualizarUsuario() throws Exception {
        UsuarioRequest request = criarRequestValido();
        UsuarioResponse response = criarResponseValido();
        when(usuarioService.atualizar(eq(1L), any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve inativar usuário e retornar 204")
    void deveInativarUsuario() throws Exception {
        doNothing().when(usuarioService).inativar(1L);

        mockMvc.perform(patch("/v1/usuarios/1/inativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve deletar usuário e retornar 204")
    void deveDeletarUsuario() throws Exception {
        doNothing().when(usuarioService).deletar(1L);

        mockMvc.perform(delete("/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}
