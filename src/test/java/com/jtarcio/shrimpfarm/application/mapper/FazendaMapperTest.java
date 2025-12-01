package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FazendaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FazendaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FazendaMapperTest {

    private final FazendaMapper mapper = new FazendaMapper();

    private FazendaRequest criarRequest() {
        FazendaRequest request = new FazendaRequest();
        request.setNome("Fazenda Teste");
        request.setProprietario("João");
        request.setCidade("Cidade X");
        request.setEstado("ES");
        // não usa areaTotalHa para evitar métodos inexistentes
        request.setAtiva(true);
        return request;
    }

    private Fazenda criarEntity() {
        Fazenda entity = new Fazenda();
        entity.setId(1L);
        entity.setNome("Fazenda Teste");
        entity.setProprietario("João");
        entity.setCidade("Cidade X");
        entity.setEstado("ES");
        entity.setAtiva(true);
        return entity;
    }

    @Test
    @DisplayName("Deve converter FazendaRequest para Fazenda")
    void deveConverterRequestParaEntity() {
        FazendaRequest request = criarRequest();

        Fazenda entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getNome()).isEqualTo(request.getNome());
        assertThat(entity.getProprietario()).isEqualTo(request.getProprietario());
        assertThat(entity.getCidade()).isEqualTo(request.getCidade());
        assertThat(entity.getEstado()).isEqualTo(request.getEstado());
        assertThat(entity.getAtiva()).isEqualTo(request.getAtiva());
    }

    @Test
    @DisplayName("Deve converter Fazenda para FazendaResponse")
    void deveConverterEntityParaResponse() {
        Fazenda entity = criarEntity();

        FazendaResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getNome()).isEqualTo(entity.getNome());
        assertThat(response.getProprietario()).isEqualTo(entity.getProprietario());
        assertThat(response.getCidade()).isEqualTo(entity.getCidade());
        assertThat(response.getEstado()).isEqualTo(entity.getEstado());
        assertThat(response.getAtiva()).isEqualTo(entity.getAtiva());
    }

    @Test
    @DisplayName("Deve atualizar Fazenda existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Fazenda entity = criarEntity();
        FazendaRequest request = criarRequest();

        request.setNome("Fazenda Atualizada");
        request.setCidade("Cidade Y");
        request.setAtiva(false);

        mapper.updateEntity(entity, request);

        assertThat(entity.getNome()).isEqualTo("Fazenda Atualizada");
        assertThat(entity.getCidade()).isEqualTo("Cidade Y");
        assertThat(entity.getAtiva()).isFalse();
        // demais campos continuam coerentes com o request original
        assertThat(entity.getProprietario()).isEqualTo(request.getProprietario());
        assertThat(entity.getEstado()).isEqualTo(request.getEstado());
    }
}
