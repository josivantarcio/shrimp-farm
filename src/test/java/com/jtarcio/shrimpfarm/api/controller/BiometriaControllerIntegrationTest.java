package com.jtarcio.shrimpfarm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.infrastructure.persistence.BiometriaRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.LoteRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.ViveiroRepository;
import com.jtarcio.shrimpfarm.infrastructure.persistence.FazendaRepository;
import com.jtarcio.shrimpfarm.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes de Integração - BiometriaController")
class BiometriaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BiometriaRepository biometriaRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ViveiroRepository viveiroRepository;

    @Autowired
    private FazendaRepository fazendaRepository;

    private Lote loteAtivo;
    private LocalDate dataPovoamento;

    @BeforeEach
    void setUp() {
        biometriaRepository.deleteAll();
        loteRepository.deleteAll();
        viveiroRepository.deleteAll();
        fazendaRepository.deleteAll();

        // Criar estrutura: Fazenda -> Viveiro -> Lote
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();
        fazenda = fazendaRepository.save(fazenda);

        Viveiro viveiro = Viveiro.builder()
                .nome("Viveiro 1")
                .codigo("V01")
                .area(BigDecimal.valueOf(1000))
                .fazenda(fazenda)
                .build();
        viveiro = viveiroRepository.save(viveiro);

        dataPovoamento = LocalDate.now().minusDays(30);

        loteAtivo = Lote.builder()
                .dataPovoamento(dataPovoamento)
                .quantidadePosLarvas(10000)
                .codigo("L001")
                .viveiro(viveiro)
                .build();
        loteAtivo = loteRepository.save(loteAtivo);
    }

    @Test
    @DisplayName("Deve criar uma biometria com sucesso")
    void deveCriarBiometriaComSucesso() throws Exception {
        BiometriaRequest request = BiometriaRequest.builder()
                .loteId(loteAtivo.getId())
                .dataBiometria(LocalDate.now())
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build();

        mockMvc.perform(post("/v1/biometrias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pesoMedio").value(15.5))
                .andExpect(jsonPath("$.quantidadeAmostrada").value(50));
    }

    @Test
    @DisplayName("Deve retornar 400 quando campos obrigatórios estiverem ausentes")
    void deveValidarCamposObrigatorios() throws Exception {
        BiometriaRequest request = BiometriaRequest.builder()
                // loteId ausente
                .dataBiometria(LocalDate.now())
                .build();

        mockMvc.perform(post("/v1/biometrias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 quando lote não existe")
    void deveRetornar404QuandoLoteNaoExiste() throws Exception {
        BiometriaRequest request = BiometriaRequest.builder()
                .loteId(99999L) // ID inexistente
                .dataBiometria(LocalDate.now())
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build();

        mockMvc.perform(post("/v1/biometrias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar biometria por ID")
    void deveBuscarBiometriaPorId() throws Exception {
        LocalDate dataBiometria = LocalDate.now();
        int diaCultivo = (int) ChronoUnit.DAYS.between(dataPovoamento, dataBiometria);

        Biometria biometria = Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(dataBiometria)
                .diaCultivo(diaCultivo)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build();
        biometria = biometriaRepository.save(biometria);

        mockMvc.perform(get("/v1/biometrias/{id}", biometria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(biometria.getId()))
                .andExpect(jsonPath("$.pesoMedio").value(15.5));
    }

    @Test
    @DisplayName("Deve listar biometrias por lote")
    void deveListarBiometriasPorLote() throws Exception {
        // Criar 2 biometrias para o mesmo lote
        LocalDate data1 = LocalDate.now().minusDays(10);
        int dia1 = (int) ChronoUnit.DAYS.between(dataPovoamento, data1);

        biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(data1)
                .diaCultivo(dia1)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(10.0))
                .quantidadeAmostrada(50)
                .build());

        LocalDate data2 = LocalDate.now();
        int dia2 = (int) ChronoUnit.DAYS.between(dataPovoamento, data2);

        biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(data2)
                .diaCultivo(dia2)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build());

        mockMvc.perform(get("/v1/biometrias/lote/{loteId}", loteAtivo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pesoMedio").exists())
                .andExpect(jsonPath("$[1].pesoMedio").exists());
    }

    @Test
    @DisplayName("Deve buscar última biometria do lote")
    void deveBuscarUltimaBiometriaDoLote() throws Exception {
        LocalDate data1 = LocalDate.now().minusDays(10);
        int dia1 = (int) ChronoUnit.DAYS.between(dataPovoamento, data1);

        biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(data1)
                .diaCultivo(dia1)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(10.0))
                .quantidadeAmostrada(50)
                .build());

        LocalDate data2 = LocalDate.now();
        int dia2 = (int) ChronoUnit.DAYS.between(dataPovoamento, data2);

        Biometria ultima = biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(data2)
                .diaCultivo(dia2)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build());

        mockMvc.perform(get("/v1/biometrias/lote/{loteId}/ultima", loteAtivo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pesoMedio").value(15.5));
    }

    @Test
    @DisplayName("Deve atualizar biometria")
    void deveAtualizarBiometria() throws Exception {
        LocalDate dataBiometria = LocalDate.now();
        int diaCultivo = (int) ChronoUnit.DAYS.between(dataPovoamento, dataBiometria);

        Biometria biometria = biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(dataBiometria)
                .diaCultivo(diaCultivo)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build());

        BiometriaRequest request = BiometriaRequest.builder()
                .loteId(loteAtivo.getId())
                .dataBiometria(LocalDate.now())
                .pesoMedio(BigDecimal.valueOf(18.0))
                .quantidadeAmostrada(60)
                .build();

        mockMvc.perform(put("/v1/biometrias/{id}", biometria.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pesoMedio").value(18.0))
                .andExpect(jsonPath("$.quantidadeAmostrada").value(60));
    }

    @Test
    @DisplayName("Deve deletar biometria")
    void deveDeletarBiometria() throws Exception {
        LocalDate dataBiometria = LocalDate.now();
        int diaCultivo = (int) ChronoUnit.DAYS.between(dataPovoamento, dataBiometria);

        Biometria biometria = biometriaRepository.save(Biometria.builder()
                .lote(loteAtivo)
                .dataBiometria(dataBiometria)
                .diaCultivo(diaCultivo)  // ✅ CAMPO OBRIGATÓRIO
                .pesoMedio(BigDecimal.valueOf(15.5))
                .quantidadeAmostrada(50)
                .build());

        mockMvc.perform(delete("/v1/biometrias/{id}", biometria.getId()))
                .andExpect(status().isNoContent());

        // Verificar que foi deletado
        mockMvc.perform(get("/v1/biometrias/{id}", biometria.getId()))
                .andExpect(status().isNotFound());
    }
}
