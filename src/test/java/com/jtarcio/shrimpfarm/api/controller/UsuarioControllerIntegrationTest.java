package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.domain.enums.RoleEnum;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioRequest criarRequestBasico(String email) {
        return UsuarioRequest.builder()
                .nome("João Silva")
                .email(email)
                .senha("senha123456")
                .papel(RoleEnum.ADMIN)
                .build();
    }

    @Test
    void deveCriarUsuarioComSucesso() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        String resposta = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(get("/v1/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("joao@teste.com"));
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/usuarios/email/{email}", "joao@teste.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@teste.com"));
    }

    @Test
    void deveBuscarUsuariosAtivos() throws Exception {
        UsuarioRequest request1 = criarRequestBasico("joao@teste.com");
        UsuarioRequest request2 = criarRequestBasico("maria@teste.com");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/usuarios/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveListarTodosUsuarios() throws Exception {
        UsuarioRequest request1 = criarRequestBasico("joao@teste.com");
        UsuarioRequest request2 = criarRequestBasico("maria@teste.com");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveAtualizarUsuarioComSucesso() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        String resposta = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        UsuarioRequest atualizacao = UsuarioRequest.builder()
                .nome("João Silva Atualizado")
                .email("joao@teste.com")
                .senha("senhaAtualizada123")
                .papel(RoleEnum.ADMIN)
                .build();

        mockMvc.perform(put("/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));
    }

    @Test
    void deveImpedirCriacaoComEmailDuplicado() throws Exception {
        UsuarioRequest primeiro = criarRequestBasico("joao@teste.com");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiro)))
                .andExpect(status().isCreated());

        UsuarioRequest segundo = UsuarioRequest.builder()
                .nome("João Silva Duplicado")
                .email("joao@teste.com")
                .senha("outraSenha123")
                .papel(RoleEnum.ADMIN)
                .build();

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundo)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void deveDeletarUsuario() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        String resposta = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(delete("/v1/usuarios/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404ParaUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/v1/usuarios/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AposDeletar() throws Exception {
        UsuarioRequest request = criarRequestBasico("joao@teste.com");

        String resposta = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(delete("/v1/usuarios/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/usuarios/{id}", id))
                .andExpect(status().isNotFound());
    }
}
