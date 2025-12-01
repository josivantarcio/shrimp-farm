package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.BiometriaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.BiometriaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class BiometriaMapperTest {

    private final BiometriaMapper mapper = new BiometriaMapper();

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(1L);
        lote.setCodigo("L001");
        lote.setDataPovoamento(LocalDate.of(2025, 3, 1));
        return lote;
    }

    private BiometriaRequest criarRequest() {
        return BiometriaRequest.builder()
                .loteId(1L)
                .dataBiometria(LocalDate.of(2025, 3, 10))
                .pesoMedio(new BigDecimal("25.3"))
                .quantidadeAmostrada(1500)
                .pesoTotalAmostra(new BigDecimal("38250.0"))
                .observacoes("Observação teste biometria")
                .build();
    }

    private Biometria criarEntity() {
        Lote lote = criarLote();
        int diaCultivo = (int) ChronoUnit.DAYS.between(lote.getDataPovoamento(), LocalDate.of(2025, 3, 10));
        return Biometria.builder()
                .id(100L)
                .lote(lote)
                .dataBiometria(LocalDate.of(2025, 3, 10))
                .diaCultivo(diaCultivo)
                .pesoMedio(new BigDecimal("25.3"))
                .quantidadeAmostrada(1500)
                .pesoTotalAmostra(new BigDecimal("38250.0"))
                .observacoes("Observação teste biometria")
                .dataCriacao(LocalDateTime.of(2025, 3, 11, 10, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 12, 11, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter BiometriaRequest para Biometria")
    void deveConverterRequestParaEntity() {
        BiometriaRequest request = criarRequest();
        Lote lote = criarLote();

        Biometria entity = mapper.toEntity(request, lote);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getDataBiometria()).isEqualTo(request.getDataBiometria());
        int esperadoDiaCultivo = (int) ChronoUnit.DAYS.between(lote.getDataPovoamento(), request.getDataBiometria());
        assertThat(entity.getDiaCultivo()).isEqualTo(esperadoDiaCultivo);
        assertThat(entity.getPesoMedio()).isEqualTo(request.getPesoMedio());
        assertThat(entity.getQuantidadeAmostrada()).isEqualTo(request.getQuantidadeAmostrada());
        assertThat(entity.getPesoTotalAmostra()).isEqualTo(request.getPesoTotalAmostra());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Biometria existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Biometria entity = criarEntity();

        BiometriaRequest request = BiometriaRequest.builder()
                .loteId(2L)
                .dataBiometria(LocalDate.of(2025, 4, 1))
                .pesoMedio(new BigDecimal("27.5"))
                .quantidadeAmostrada(1800)
                .pesoTotalAmostra(new BigDecimal("49500.0"))
                .observacoes("Observação atualizada biometria")
                .build();

        Lote novoLote = new Lote();
        novoLote.setId(2L);
        novoLote.setCodigo("L002");
        novoLote.setDataPovoamento(LocalDate.of(2025, 3, 5));

        mapper.updateEntity(entity, request, novoLote);

        int esperadoDiaCultivo = (int) ChronoUnit.DAYS.between(novoLote.getDataPovoamento(), request.getDataBiometria());

        assertThat(entity.getLote()).isSameAs(novoLote);
        assertThat(entity.getDataBiometria()).isEqualTo(request.getDataBiometria());
        assertThat(entity.getDiaCultivo()).isEqualTo(esperadoDiaCultivo);
        assertThat(entity.getPesoMedio()).isEqualTo(request.getPesoMedio());
        assertThat(entity.getQuantidadeAmostrada()).isEqualTo(request.getQuantidadeAmostrada());
        assertThat(entity.getPesoTotalAmostra()).isEqualTo(request.getPesoTotalAmostra());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Biometria para BiometriaResponse")
    void deveConverterEntityParaResponse() {
        Biometria entity = criarEntity();

        BiometriaResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getLoteCodigo()).isEqualTo(entity.getLote().getCodigo());
        assertThat(response.getDataBiometria()).isEqualTo(entity.getDataBiometria());
        assertThat(response.getDiaCultivo()).isEqualTo(entity.getDiaCultivo());
        assertThat(response.getPesoMedio()).isEqualTo(entity.getPesoMedio());
        assertThat(response.getQuantidadeAmostrada()).isEqualTo(entity.getQuantidadeAmostrada());
        assertThat(response.getPesoTotalAmostra()).isEqualTo(entity.getPesoTotalAmostra());
        assertThat(response.getGanhoPesoDiario()).isEqualTo(entity.getGanhoPesoDiario());
        assertThat(response.getBiomassaEstimada()).isEqualTo(entity.getBiomassaEstimada());
        assertThat(response.getSobrevivenciaEstimada()).isEqualTo(entity.getSobrevivenciaEstimada());
        assertThat(response.getFatorConversaoAlimentar()).isEqualTo(entity.getFatorConversaoAlimentar());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
