package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.FertilizacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.FertilizacaoResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FertilizacaoMapperTest {

    private final FertilizacaoMapper mapper = new FertilizacaoMapper();

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(1L);
        lote.setCodigo("L001");
        return lote;
    }

    private Fornecedor criarFornecedor() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(10L);
        fornecedor.setNome("Fornecedor A");
        return fornecedor;
    }

    private FertilizacaoRequest criarRequest() {
        return FertilizacaoRequest.builder()
                .loteId(1L)
                .fornecedorId(10L)
                .dataAplicacao(LocalDate.of(2025, 3, 10))
                .produto("Produto Fertilizante")
                .quantidade(new BigDecimal("100.500"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("25.60"))
                .finalidade("Correção de nutrientes")
                .observacoes("Obs teste fertilização")
                .build();
    }

    private Fertilizacao criarEntity() {
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        return Fertilizacao.builder()
                .id(100L)
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2025, 3, 10))
                .produto("Produto Fertilizante")
                .quantidade(new BigDecimal("100.500"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("25.60"))
                .custoTotal(new BigDecimal("2576.80"))
                .finalidade("Correção de nutrientes")
                .observacoes("Obs teste fertilização")
                .dataCriacao(LocalDateTime.of(2025, 3, 11, 8, 0))
                .dataAtualizacao(LocalDateTime.of(2025, 3, 12, 9, 30))
                .build();
    }

    @Test
    @DisplayName("Deve converter FertilizacaoRequest para Fertilizacao")
    void deveConverterRequestParaEntity() {
        FertilizacaoRequest request = criarRequest();
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        Fertilizacao entity = mapper.toEntity(request, lote, fornecedor);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getFornecedor()).isSameAs(fornecedor);
        assertThat(entity.getDataAplicacao()).isEqualTo(request.getDataAplicacao());
        assertThat(entity.getProduto()).isEqualTo(request.getProduto());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getFinalidade()).isEqualTo(request.getFinalidade());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Fertilizacao existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Fertilizacao entity = criarEntity();

        FertilizacaoRequest request = FertilizacaoRequest.builder()
                .loteId(2L)
                .fornecedorId(20L)
                .dataAplicacao(LocalDate.of(2025, 4, 1))
                .produto("Produto Atualizado")
                .quantidade(new BigDecimal("200.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("30.00"))
                .finalidade("Manutenção da fertilidade")
                .observacoes("Obs atualizada fertilização")
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
        assertThat(entity.getProduto()).isEqualTo(request.getProduto());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getFinalidade()).isEqualTo(request.getFinalidade());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Fertilizacao para FertilizacaoResponse")
    void deveConverterEntityParaResponse() {
        Fertilizacao entity = criarEntity();

        FertilizacaoResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getLoteCodigo()).isEqualTo(entity.getLote().getCodigo());
        assertThat(response.getFornecedorId()).isEqualTo(entity.getFornecedor().getId());
        assertThat(response.getFornecedorNome()).isEqualTo(entity.getFornecedor().getNome());
        assertThat(response.getDataAplicacao()).isEqualTo(entity.getDataAplicacao());
        assertThat(response.getProduto()).isEqualTo(entity.getProduto());
        assertThat(response.getQuantidade()).isEqualTo(entity.getQuantidade());
        assertThat(response.getUnidade()).isEqualTo(entity.getUnidade());
        assertThat(response.getCustoUnitario()).isEqualTo(entity.getCustoUnitario());
        assertThat(response.getCustoTotal()).isEqualTo(entity.getCustoTotal());
        assertThat(response.getFinalidade()).isEqualTo(entity.getFinalidade());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
        assertThat(response.getDataCriacao()).isEqualTo(entity.getDataCriacao());
        assertThat(response.getDataAtualizacao()).isEqualTo(entity.getDataAtualizacao());
    }
}
