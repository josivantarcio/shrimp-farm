package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.CustoVariavelRequest;
import com.jtarcio.shrimpfarm.application.dto.response.CustoVariavelResponse;
import com.jtarcio.shrimpfarm.domain.entity.CustoVariavel;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustoVariavelMapperTest {

    private final CustoVariavelMapper mapper = new CustoVariavelMapper();

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(1L);
        lote.setCodigo("L001");
        return lote;
    }

    private CustoVariavelRequest criarRequest() {
        return CustoVariavelRequest.builder()
                .loteId(1L)
                .dataLancamento(LocalDate.of(2025, 3, 10))
                .categoria(CategoriaGastoEnum.RACAO)
                .descricao("Compra de ração para teste")
                .valor(new BigDecimal("1234.56"))
                .observacoes("Observação teste custo variável")
                .build();
    }

    private CustoVariavel criarEntity() {
        Lote lote = criarLote();
        return CustoVariavel.builder()
                .id(100L)
                .lote(lote)
                .dataLancamento(LocalDate.of(2025, 3, 10))
                .categoria(CategoriaGastoEnum.RACAO)
                .descricao("Compra de ração para teste")
                .valor(new BigDecimal("1234.56"))
                .observacoes("Observação teste custo variável")
                .dataCriacao(LocalDateTime.of(2025, 3, 11, 10, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 12, 11, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter CustoVariavelRequest para CustoVariavel")
    void deveConverterRequestParaEntity() {
        CustoVariavelRequest request = criarRequest();
        Lote lote = criarLote();

        CustoVariavel entity = mapper.toEntity(request, lote);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getDataLancamento()).isEqualTo(request.getDataLancamento());
        assertThat(entity.getCategoria()).isEqualTo(request.getCategoria());
        assertThat(entity.getDescricao()).isEqualTo(request.getDescricao());
        assertThat(entity.getValor()).isEqualTo(request.getValor());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar CustoVariavel existente com dados do request")
    void deveAtualizarEntityComRequest() {
        CustoVariavel entity = criarEntity();

        CustoVariavelRequest request = CustoVariavelRequest.builder()
                .loteId(2L)
                .dataLancamento(LocalDate.of(2025, 4, 1))
                .categoria(CategoriaGastoEnum.ENERGIA)
                .descricao("Conta de energia atualizada")
                .valor(new BigDecimal("2500.00"))
                .observacoes("Observação atualizada custo energia")
                .build();

        Lote novoLote = new Lote();
        novoLote.setId(2L);
        novoLote.setCodigo("L002");

        mapper.updateEntity(entity, request, novoLote);

        assertThat(entity.getLote()).isSameAs(novoLote);
        assertThat(entity.getDataLancamento()).isEqualTo(request.getDataLancamento());
        assertThat(entity.getCategoria()).isEqualTo(request.getCategoria());
        assertThat(entity.getDescricao()).isEqualTo(request.getDescricao());
        assertThat(entity.getValor()).isEqualTo(request.getValor());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter CustoVariavel para CustoVariavelResponse")
    void deveConverterEntityParaResponse() {
        CustoVariavel entity = criarEntity();

        CustoVariavelResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getLoteCodigo()).isEqualTo(entity.getLote().getCodigo());
        assertThat(response.getDataLancamento()).isEqualTo(entity.getDataLancamento());
        assertThat(response.getCategoria()).isEqualTo(entity.getCategoria());
        assertThat(response.getDescricao()).isEqualTo(entity.getDescricao());
        assertThat(response.getValor()).isEqualTo(entity.getValor());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
