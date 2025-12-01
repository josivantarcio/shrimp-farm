package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.DespescaRequest;
import com.jtarcio.shrimpfarm.application.dto.response.DespescaResponse;
import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DespescaMapperTest {

    private final DespescaMapper mapper = new DespescaMapper();

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(1L);
        lote.setCodigo("L001");
        return lote;
    }

    private Comprador criarComprador() {
        Comprador comprador = new Comprador();
        comprador.setId(10L);
        comprador.setNome("Comprador A");
        return comprador;
    }

    private DespescaRequest criarRequest() {
        return DespescaRequest.builder()
                .loteId(1L)
                .compradorId(10L)
                .dataDespesca(LocalDate.of(2025, 3, 10))
                .pesoTotal(new BigDecimal("1000.50"))
                .quantidadeDespescada(50000)
                .pesoMedioFinal(new BigDecimal("20.010"))
                .precoVendaKg(new BigDecimal("25.60"))
                .custoDespesca(new BigDecimal("1500.00"))
                .observacoes("Obs teste despesca")
                .build();
    }

    private Despesca criarEntity() {
        Lote lote = criarLote();
        Comprador comprador = criarComprador();

        return Despesca.builder()
                .id(100L)
                .lote(lote)
                .comprador(comprador)
                .dataDespesca(LocalDate.of(2025, 3, 10))
                .pesoTotal(new BigDecimal("1000.50"))
                .quantidadeDespescada(50000)
                .pesoMedioFinal(new BigDecimal("20.010"))
                .taxaSobrevivencia(new BigDecimal("85.50"))
                .precoVendaKg(new BigDecimal("25.60"))
                .receitaTotal(new BigDecimal("25600.00"))
                .custoDespesca(new BigDecimal("1500.00"))
                .observacoes("Obs teste despesca")
                .dataCriacao(LocalDateTime.of(2025, 3, 11, 8, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 12, 9, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter DespescaRequest para Despesca")
    void deveConverterRequestParaEntity() {
        DespescaRequest request = criarRequest();
        Lote lote = criarLote();
        Comprador comprador = criarComprador();

        Despesca entity = mapper.toEntity(request, lote, comprador);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getComprador()).isSameAs(comprador);
        assertThat(entity.getDataDespesca()).isEqualTo(request.getDataDespesca());
        assertThat(entity.getPesoTotal()).isEqualTo(request.getPesoTotal());
        assertThat(entity.getQuantidadeDespescada()).isEqualTo(request.getQuantidadeDespescada());
        assertThat(entity.getPesoMedioFinal()).isEqualTo(request.getPesoMedioFinal());
        assertThat(entity.getPrecoVendaKg()).isEqualTo(request.getPrecoVendaKg());
        assertThat(entity.getCustoDespesca()).isEqualTo(request.getCustoDespesca());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Despesca existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Despesca entity = criarEntity();
        DespescaRequest request = DespescaRequest.builder()
                .loteId(2L) // id do lote é ignorado, lote vem como parâmetro
                .compradorId(20L)
                .dataDespesca(LocalDate.of(2025, 4, 1))
                .pesoTotal(new BigDecimal("1200.00"))
                .quantidadeDespescada(60000)
                .pesoMedioFinal(new BigDecimal("22.000"))
                .precoVendaKg(new BigDecimal("30.00"))
                .custoDespesca(new BigDecimal("1600.00"))
                .observacoes("Obs atualizada despesca")
                .build();

        Lote novoLote = new Lote();
        novoLote.setId(2L);
        novoLote.setCodigo("L002");

        Comprador novoComprador = new Comprador();
        novoComprador.setId(20L);
        novoComprador.setNome("Comprador B");

        mapper.updateEntity(entity, request, novoLote, novoComprador);

        assertThat(entity.getLote()).isSameAs(novoLote);
        assertThat(entity.getComprador()).isSameAs(novoComprador);
        assertThat(entity.getDataDespesca()).isEqualTo(request.getDataDespesca());
        assertThat(entity.getPesoTotal()).isEqualTo(request.getPesoTotal());
        assertThat(entity.getQuantidadeDespescada()).isEqualTo(request.getQuantidadeDespescada());
        assertThat(entity.getPesoMedioFinal()).isEqualTo(request.getPesoMedioFinal());
        assertThat(entity.getPrecoVendaKg()).isEqualTo(request.getPrecoVendaKg());
        assertThat(entity.getCustoDespesca()).isEqualTo(request.getCustoDespesca());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Despesca para DespescaResponse")
    void deveConverterEntityParaResponse() {
        Despesca entity = criarEntity();

        DespescaResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getLoteCodigo()).isEqualTo(entity.getLote().getCodigo());
        assertThat(response.getCompradorId()).isEqualTo(entity.getComprador().getId());
        assertThat(response.getCompradorNome()).isEqualTo(entity.getComprador().getNome());
        assertThat(response.getDataDespesca()).isEqualTo(entity.getDataDespesca());
        assertThat(response.getPesoTotal()).isEqualTo(entity.getPesoTotal());
        assertThat(response.getQuantidadeDespescada()).isEqualTo(entity.getQuantidadeDespescada());
        assertThat(response.getPesoMedioFinal()).isEqualTo(entity.getPesoMedioFinal());
        assertThat(response.getTaxaSobrevivencia()).isEqualTo(entity.getTaxaSobrevivencia());
        assertThat(response.getPrecoVendaKg()).isEqualTo(entity.getPrecoVendaKg());
        assertThat(response.getReceitaTotal()).isEqualTo(entity.getReceitaTotal());
        assertThat(response.getCustoDespesca()).isEqualTo(entity.getCustoDespesca());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
