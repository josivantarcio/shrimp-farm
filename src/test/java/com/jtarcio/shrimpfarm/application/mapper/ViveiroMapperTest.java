package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.ViveiroRequest;
import com.jtarcio.shrimpfarm.application.dto.response.ViveiroResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ViveiroMapperTest {

    private final ViveiroMapper mapper = new ViveiroMapper();

    private Fazenda criarFazenda() {
        Fazenda fazenda = new Fazenda();
        fazenda.setId(1L);
        fazenda.setNome("Fazenda Teste");
        return fazenda;
    }

    private ViveiroRequest criarRequest() {
        return ViveiroRequest.builder()
                .fazendaId(1L)
                .codigo("V001")
                .nome("Viveiro Teste")
                .area(new BigDecimal("1.5"))
                .profundidadeMedia(new BigDecimal("1.8"))
                .volume(new BigDecimal("2700.0"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Observação teste viveiro")
                .ativo(true)
                .build();
    }

    private Viveiro criarEntity() {
        Fazenda fazenda = criarFazenda();
        return Viveiro.builder()
                .id(100L)
                .fazenda(fazenda)
                .codigo("V001")
                .nome("Viveiro Teste")
                .area(new BigDecimal("1.5"))
                .profundidadeMedia(new BigDecimal("1.8"))
                .volume(new BigDecimal("2700.0"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Observação teste viveiro")
                .ativo(true)
                .dataCriacao(LocalDateTime.of(2025, 3, 10, 14, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 11, 15, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter ViveiroRequest para Viveiro")
    void deveConverterRequestParaEntity() {
        ViveiroRequest request = criarRequest();
        Fazenda fazenda = criarFazenda();

        Viveiro entity = mapper.toEntity(request, fazenda);

        assertThat(entity).isNotNull();
        assertThat(entity.getFazenda()).isSameAs(fazenda);
        assertThat(entity.getCodigo()).isEqualTo(request.getCodigo());
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getArea()).isEqualTo(request.getArea());
        assertThat(entity.getProfundidadeMedia()).isEqualTo(request.getProfundidadeMedia());
        assertThat(entity.getVolume()).isEqualTo(request.getVolume());
        assertThat(entity.getStatus()).isEqualTo(request.getStatus());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
        assertThat(entity.getAtivo()).isEqualTo(request.getAtivo());
    }

    @Test
    @DisplayName("Deve atualizar Viveiro existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Viveiro entity = criarEntity();

        ViveiroRequest request = ViveiroRequest.builder()
                .fazendaId(2L)
                .codigo("V002")
                .nome("Viveiro Atualizado")
                .area(new BigDecimal("2.0"))
                .profundidadeMedia(new BigDecimal("2.0"))
                .volume(new BigDecimal("4000.0"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Observação atualizada viveiro")
                .ativo(false)
                .build();

        Fazenda novaFazenda = new Fazenda();
        novaFazenda.setId(2L);
        novaFazenda.setNome("Fazenda Nova");

        mapper.updateEntity(entity, request, novaFazenda);

        assertThat(entity.getFazenda()).isSameAs(novaFazenda);
        assertThat(entity.getCodigo()).isEqualTo(request.getCodigo());
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getArea()).isEqualTo(request.getArea());
        assertThat(entity.getProfundidadeMedia()).isEqualTo(request.getProfundidadeMedia());
        assertThat(entity.getVolume()).isEqualTo(request.getVolume());
        assertThat(entity.getStatus()).isEqualTo(request.getStatus());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
        assertThat(entity.getAtivo()).isEqualTo(request.getAtivo());
    }

    @Test
    @DisplayName("Deve converter Viveiro para ViveiroResponse")
    void deveConverterEntityParaResponse() {
        Viveiro entity = criarEntity();

        ViveiroResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getFazendaId()).isEqualTo(entity.getFazenda().getId());
        assertThat(response.getFazendaNome()).isEqualTo(entity.getFazenda().getNome());
        assertThat(response.getCodigo()).isEqualTo(entity.getCodigo());
        assertThat(response.getNome()).isEqualTo(entity.getNome());
        assertThat(response.getArea()).isEqualTo(entity.getArea());
        assertThat(response.getProfundidadeMedia()).isEqualTo(entity.getProfundidadeMedia());
        assertThat(response.getVolume()).isEqualTo(entity.getVolume());
        assertThat(response.getStatus()).isEqualTo(entity.getStatus());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getAtivo()).isEqualTo(entity.getAtivo());
        assertThat(response.getQuantidadeLotes()).isEqualTo(entity.getLotes().size());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}