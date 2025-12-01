package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.NutrienteRequest;
import com.jtarcio.shrimpfarm.application.dto.response.NutrienteResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import com.jtarcio.shrimpfarm.domain.enums.TipoNutrienteEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NutrienteMapperTest {

    private final NutrienteMapper mapper = new NutrienteMapper();

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(1L);
        lote.setCodigo("L001");
        return lote;
    }

    private Fornecedor criarFornecedor() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(10L);
        fornecedor.setNome("Fornecedor Nutriente");
        return fornecedor;
    }

    private NutrienteRequest criarRequest() {
        return NutrienteRequest.builder()
                .loteId(1L)
                .fornecedorId(10L)
                .dataAplicacao(LocalDate.of(2025, 1, 15))
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Produto X")
                .quantidade(new BigDecimal("25.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("4.50"))
                .observacoes("Obs nutriente teste")
                .build();
    }

    private Nutriente criarEntity() {
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        return Nutriente.builder()
                .id(100L)
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2025, 1, 15))
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Produto X")
                .quantidade(new BigDecimal("25.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("4.50"))
                .custoTotal(new BigDecimal("112.50"))
                .observacoes("Obs nutriente teste")
                .dataCriacao(LocalDateTime.of(2025, 1, 16, 8, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 1, 17, 9, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter NutrienteRequest para Nutriente")
    void deveConverterRequestParaEntity() {
        NutrienteRequest request = criarRequest();
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        Nutriente entity = mapper.toEntity(request, lote, fornecedor);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getFornecedor()).isSameAs(fornecedor);
        assertThat(entity.getDataAplicacao()).isEqualTo(request.getDataAplicacao());
        assertThat(entity.getTipoNutriente()).isEqualTo(request.getTipoNutriente());
        assertThat(entity.getProduto()).isEqualTo(request.getProduto());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Nutriente existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Nutriente entity = criarEntity();
        NutrienteRequest request = NutrienteRequest.builder()
                .loteId(2L)          // IDs no request são ignorados, Lote/Fornecedor vêm por parâmetro
                .fornecedorId(20L)
                .dataAplicacao(LocalDate.of(2025, 2, 1))
                .tipoNutriente(TipoNutrienteEnum.MELHORADOR_AGUA)
                .produto("Produto Y")
                .quantidade(new BigDecimal("30.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("5.00"))
                .observacoes("Obs nutriente atualizada")
                .build();

        Lote novoLote = new Lote();
        novoLote.setId(2L);
        novoLote.setCodigo("L002");

        Fornecedor novoFornecedor = new Fornecedor();
        novoFornecedor.setId(20L);
        novoFornecedor.setNome("Fornecedor B");

        mapper.updateEntity(entity, request, novoLote, novoFornecedor);

        assertThat(entity.getLote()).isSameAs(novoLote);
        assertThat(entity.getFornecedor()).isSameAs(novoFornecedor);
        assertThat(entity.getDataAplicacao()).isEqualTo(request.getDataAplicacao());
        assertThat(entity.getTipoNutriente()).isEqualTo(request.getTipoNutriente());
        assertThat(entity.getProduto()).isEqualTo(request.getProduto());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Nutriente para NutrienteResponse")
    void deveConverterEntityParaResponse() {
        Nutriente entity = criarEntity();

        NutrienteResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getLoteCodigo()).isEqualTo(entity.getLote().getCodigo());
        assertThat(response.getFornecedorId()).isEqualTo(entity.getFornecedor().getId());
        assertThat(response.getFornecedorNome()).isEqualTo(entity.getFornecedor().getNome());
        assertThat(response.getDataAplicacao()).isEqualTo(entity.getDataAplicacao());
        assertThat(response.getTipoNutriente()).isEqualTo(entity.getTipoNutriente());
        assertThat(response.getProduto()).isEqualTo(entity.getProduto());
        assertThat(response.getQuantidade()).isEqualTo(entity.getQuantidade());
        assertThat(response.getUnidade()).isEqualTo(entity.getUnidade());
        assertThat(response.getCustoUnitario()).isEqualTo(entity.getCustoUnitario());
        assertThat(response.getCustoTotal()).isEqualTo(entity.getCustoTotal());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
