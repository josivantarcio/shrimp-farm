package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DespescaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Long criarFazendaMinima() throws Exception {
        String json = """
                {
                  "nome": "Fazenda Teste",
                  "proprietario": "Teste",
                  "endereco": "Endereco",
                  "cidade": "Cidade",
                  "estado": "CE",
                  "cep": "00000-000",
                  "areaTotal": 10.0,
                  "areaUtil": 8.0,
                  "telefone": "(85) 98765-4321",
                  "email": "fazenda@teste.com"
                }
                """;

        String resp = mockMvc.perform(post("/v1/fazendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    private Long criarViveiroMinimo(Long fazendaId) throws Exception {
        String json = """
                {
                  "fazendaId": %d,
                  "codigo": "V01",
                  "nome": "Viveiro 1",
                  "area": 1.0,
                  "profundidadeMedia": 1.5,
                  "volume": 1000.0,
                  "status": "DISPONIVEL"
                }
                """.formatted(fazendaId);

        String resp = mockMvc.perform(post("/v1/viveiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    private Long criarLoteMinimo() throws Exception {
        Long fazendaId = criarFazendaMinima();
        Long viveiroId = criarViveiroMinimo(fazendaId);

        String json = """
                {
                  "codigo": "L01",
                  "dataPovoamento": "%s",
                  "quantidadeInicial": 10000,
                  "quantidadePosLarvas": 10000,
                  "pesoMedioInicial": 1.5,
                  "viveiroId": %d
                }
                """.formatted(LocalDate.now().minusDays(90), viveiroId);

        String resp = mockMvc.perform(post("/v1/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    private DespescaRequest criarRequestBasico(Long loteId) {
        return DespescaRequest.builder()
                .loteId(loteId)
                .compradorId(null)
                .dataDespesca(LocalDate.now())
                .pesoTotal(new BigDecimal("100.00"))
                .quantidadeDespescada(10000)
                .pesoMedioFinal(new BigDecimal("10.000"))
                .precoVendaKg(new BigDecimal("25.50"))
                .custoDespesca(new BigDecimal("500.00"))
                .observacoes("Despesca de teste")
                .build();
    }

    @Test
    void deveCriarDespescaComSucesso() throws Exception {
        Long loteId = criarLoteMinimo();
        DespescaRequest request = criarRequestBasico(loteId);

        mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loteId", is(loteId.intValue())))
                .andExpect(jsonPath("$.pesoTotal").value(100.00));
    }

    @Test
    void deveBuscarDespescaPorId() throws Exception {
        Long loteId = criarLoteMinimo();
        DespescaRequest request = criarRequestBasico(loteId);

        String resposta = mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(get("/v1/despescas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.loteId", is(loteId.intValue())));
    }

    @Test
    void deveBuscarDespescaPorLote() throws Exception {
        Long loteId = criarLoteMinimo();
        DespescaRequest request = criarRequestBasico(loteId);

        mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/despescas/lote/{loteId}", loteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loteId", is(loteId.intValue())));
    }

    @Test
    void deveAtualizarDespescaComSucesso() throws Exception {
        Long loteId = criarLoteMinimo();
        DespescaRequest request = criarRequestBasico(loteId);

        String resposta = mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        DespescaRequest atualizacao = DespescaRequest.builder()
                .loteId(loteId)
                .compradorId(null)
                .dataDespesca(LocalDate.now())
                .pesoTotal(new BigDecimal("120.00"))
                .quantidadeDespescada(11000)
                .pesoMedioFinal(new BigDecimal("11.000"))
                .precoVendaKg(new BigDecimal("26.00"))
                .custoDespesca(new BigDecimal("600.00"))
                .observacoes("Despesca atualizada")
                .build();

        mockMvc.perform(put("/v1/despescas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.pesoTotal").value(120.00));
    }

    @Test
    void deveDeletarDespesca() throws Exception {
        Long loteId = criarLoteMinimo();
        DespescaRequest request = criarRequestBasico(loteId);

        String resposta = mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(delete("/v1/despescas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/despescas/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        Long loteId = criarLoteMinimo();

        DespescaRequest invalido = DespescaRequest.builder()
                .loteId(loteId)
                .dataDespesca(LocalDate.now().plusDays(1))
                .pesoTotal(new BigDecimal("0.0"))
                .quantidadeDespescada(0)
                .pesoMedioFinal(new BigDecimal("0.0"))
                .build();

        mockMvc.perform(post("/v1/despescas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404ParaDespescaInexistente() throws Exception {
        mockMvc.perform(get("/v1/despescas/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
