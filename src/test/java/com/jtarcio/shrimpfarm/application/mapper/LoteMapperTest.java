package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.LoteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.LoteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LoteMapperTest {

    private final LoteMapper mapper = new LoteMapper();

    private Viveiro criarViveiroComFazenda() {
        Fazenda fazenda = new Fazenda();
        fazenda.setId(1L);
        fazenda.setNome("Fazenda A");

        Viveiro viveiro = new Viveiro();
        viveiro.setId(2L);
        viveiro.setCodigo("V1");
        viveiro.setNome("Viveiro 1");
        viveiro.setFazenda(fazenda);
        return viveiro;
    }

    private LoteRequest criarRequest() {
        return LoteRequest.builder()
                .viveiroId(2L)
                .codigo("L001")
                .dataPovoamento(LocalDate.of(2025, 1, 10))
                .dataDespesca(LocalDate.of(2025, 3, 10))
                .quantidadePosLarvas(100000)
                .custoPosLarvas(new BigDecimal("1500.00"))
                .densidadeInicial(new BigDecimal("50.0"))
                .status(StatusLoteEnum.PLANEJADO)
                .observacoes("Obs teste")
                .build();
    }

    private Lote criarEntity() {
        Viveiro viveiro = criarViveiroComFazenda();

        return Lote.builder()
                .id(10L)
                .viveiro(viveiro)
                .codigo("L001")
                .dataPovoamento(LocalDate.of(2025, 1, 10))
                .dataDespesca(LocalDate.of(2025, 3, 10))
                .quantidadePosLarvas(100000)
                .custoPosLarvas(new BigDecimal("1500.00"))
                .densidadeInicial(new BigDecimal("50.0"))
                .status(StatusLoteEnum.PLANEJADO)
                .diasCultivo(60)
                .observacoes("Obs teste")
                .build();
    }

    @Test
    @DisplayName("Deve converter LoteRequest para Lote")
    void deveConverterRequestParaEntity() {
        LoteRequest request = criarRequest();
        Viveiro viveiro = criarViveiroComFazenda();

        Lote entity = mapper.toEntity(request, viveiro);

        assertThat(entity).isNotNull();
        assertThat(entity.getViveiro()).isSameAs(viveiro);
        assertThat(entity.getCodigo()).isEqualTo(request.getCodigo());
        assertThat(entity.getDataPovoamento()).isEqualTo(request.getDataPovoamento());
        assertThat(entity.getDataDespesca()).isEqualTo(request.getDataDespesca());
        assertThat(entity.getQuantidadePosLarvas()).isEqualTo(request.getQuantidadePosLarvas());
        assertThat(entity.getCustoPosLarvas()).isEqualTo(request.getCustoPosLarvas());
        assertThat(entity.getDensidadeInicial()).isEqualTo(request.getDensidadeInicial());
        assertThat(entity.getStatus()).isEqualTo(request.getStatus());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Lote existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Lote entity = criarEntity();
        LoteRequest request = LoteRequest.builder()
                .viveiroId(3L) // será ignorado, pois o viveiro vem como parâmetro
                .codigo("L002")
                .dataPovoamento(LocalDate.of(2025, 2, 1))
                .dataDespesca(LocalDate.of(2025, 4, 1))
                .quantidadePosLarvas(200000)
                .custoPosLarvas(new BigDecimal("2500.00"))
                .densidadeInicial(new BigDecimal("60.0"))
                .status(StatusLoteEnum.ATIVO)
                .observacoes("Obs atualizada")
                .build();

        Viveiro novoViveiro = criarViveiroComFazenda();
        novoViveiro.setId(3L);
        novoViveiro.setCodigo("V2");
        novoViveiro.getFazenda().setId(2L);
        novoViveiro.getFazenda().setNome("Fazenda B");

        mapper.updateEntity(entity, request, novoViveiro);

        assertThat(entity.getViveiro()).isSameAs(novoViveiro);
        assertThat(entity.getCodigo()).isEqualTo("L002");
        assertThat(entity.getDataPovoamento()).isEqualTo(request.getDataPovoamento());
        assertThat(entity.getDataDespesca()).isEqualTo(request.getDataDespesca());
        assertThat(entity.getQuantidadePosLarvas()).isEqualTo(request.getQuantidadePosLarvas());
        assertThat(entity.getCustoPosLarvas()).isEqualTo(request.getCustoPosLarvas());
        assertThat(entity.getDensidadeInicial()).isEqualTo(request.getDensidadeInicial());
        assertThat(entity.getStatus()).isEqualTo(request.getStatus());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Lote para LoteResponse")
    void deveConverterEntityParaResponse() {
        Lote entity = criarEntity();

        LoteResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getCodigo()).isEqualTo(entity.getCodigo());
        assertThat(response.getViveiroId()).isEqualTo(entity.getViveiro().getId());
        assertThat(response.getViveiroCodigo()).isEqualTo(entity.getViveiro().getCodigo());
        assertThat(response.getViveiroNome()).isEqualTo(entity.getViveiro().getNome());
        assertThat(response.getFazendaId()).isEqualTo(entity.getViveiro().getFazenda().getId());
        assertThat(response.getFazendaNome()).isEqualTo(entity.getViveiro().getFazenda().getNome());
        assertThat(response.getDataPovoamento()).isEqualTo(entity.getDataPovoamento());
        assertThat(response.getDataDespesca()).isEqualTo(entity.getDataDespesca());
        assertThat(response.getQuantidadePosLarvas()).isEqualTo(entity.getQuantidadePosLarvas());
        assertThat(response.getCustoPosLarvas()).isEqualTo(entity.getCustoPosLarvas());
        assertThat(response.getDensidadeInicial()).isEqualTo(entity.getDensidadeInicial());
        assertThat(response.getStatus()).isEqualTo(entity.getStatus());
        assertThat(response.getDiasCultivo()).isEqualTo(entity.getDiasCultivo());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getQuantidadeBiometrias()).isEqualTo(entity.getBiometrias().size());
    }
}
