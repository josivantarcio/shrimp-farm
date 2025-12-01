package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.CompradorRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CompradorResponse;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompradorMapperTest {

    private final CompradorMapper mapper = new CompradorMapper();

    private CompradorRequest criarRequest() {
        return CompradorRequest.builder()
                .nome("Comprador ABC Ltda")
                .cnpj("12345678000199")
                .contato("João Silva")
                .endereco("Rua das Palmeiras, 123 - Fortaleza/CE")
                .ativo(true)
                .build();
    }

    private Comprador criarEntity() {
        return Comprador.builder()
                .id(50L)
                .nome("Comprador ABC Ltda")
                .cnpj("12345678000199")
                .contato("João Silva")
                .endereco("Rua das Palmeiras, 123 - Fortaleza/CE")
                .ativo(true)
                .dataCriacao(LocalDateTime.of(2025, 3, 15, 9, 30))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 16, 10, 45))
                .build();
    }

    @Test
    @DisplayName("Deve converter CompradorRequest para Comprador")
    void deveConverterRequestParaEntity() {
        CompradorRequest request = criarRequest();

        Comprador entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getCnpj()).isEqualTo(request.getCnpj());
        assertThat(entity.getContato()).isEqualTo(request.getContato());
        assertThat(entity.getEndereco()).isEqualTo(request.getEndereco());
        assertThat(entity.getAtivo()).isEqualTo(request.getAtivo());
    }

    @Test
    @DisplayName("Deve atualizar Comprador existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Comprador entity = criarEntity();

        CompradorRequest request = CompradorRequest.builder()
                .nome("Comprador XYZ S.A.")
                .cnpj("98765432000188")
                .contato("Maria Santos")
                .endereco("Av. Beira Mar, 456 - Fortaleza/CE")
                .ativo(false)
                .build();

        mapper.updateEntity(entity, request);

        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getCnpj()).isEqualTo(request.getCnpj());
        assertThat(entity.getContato()).isEqualTo(request.getContato());
        assertThat(entity.getEndereco()).isEqualTo(request.getEndereco());
        assertThat(entity.getAtivo()).isEqualTo(request.getAtivo());
    }

    @Test
    @DisplayName("Deve converter Comprador para CompradorResponse")
    void deveConverterEntityParaResponse() {
        Comprador entity = criarEntity();

        CompradorResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getNome()).isEqualTo(entity.getNome());
        assertThat(response.getCnpj()).isEqualTo(entity.getCnpj());
        assertThat(response.getContato()).isEqualTo(entity.getContato());
        assertThat(response.getEndereco()).isEqualTo(entity.getEndereco());
        assertThat(response.getAtivo()).isEqualTo(entity.getAtivo());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
