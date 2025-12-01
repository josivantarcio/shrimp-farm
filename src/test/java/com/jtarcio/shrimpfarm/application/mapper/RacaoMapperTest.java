package com.jtarcio.shrimpfarm.application.mapper;

import com.jtarcio.shrimpfarm.application.dto.request.RacaoRequest;
import com.jtarcio.shrimpfarm.application.dto.response.RacaoResponse;
import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.entity.Racao;
import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RacaoMapperTest {

    private final RacaoMapper mapper = new RacaoMapper();

    private RacaoRequest criarRequest() {
        return new RacaoRequest(
                10L,                               // idLote
                20L,                               // idFornecedor
                LocalDate.of(2025, 1, 1),          // dataAplicacao
                TipoRacaoEnum.INICIAL,            // tipoRacao
                "Marca X",                        // marca
                new BigDecimal("25.0"),           // quantidade
                UnidadeMedidaEnum.KG,             // unidade
                new BigDecimal("4.02"),           // custoUnitario
                new BigDecimal("38.5"),           // proteinaPercentual
                "Obs teste"                       // observacoes
        );
    }

    private Lote criarLote() {
        Lote lote = new Lote();
        lote.setId(10L);
        return lote;
    }

    private Fornecedor criarFornecedor() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(20L);
        fornecedor.setNome("Fornecedor X");
        return fornecedor;
    }

    private Racao criarEntity() {
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        return Racao.builder()
                .id(1L)
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2025, 1, 1))
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Marca X")
                .quantidade(new BigDecimal("25.0"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("4.02"))
                .proteinaPercentual(new BigDecimal("38.5"))
                .observacoes("Obs teste")
                .custoTotal(new BigDecimal("100.50"))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve converter RacaoRequest para Racao")
    void deveConverterRequestParaEntity() {
        RacaoRequest request = criarRequest();
        Lote lote = criarLote();
        Fornecedor fornecedor = criarFornecedor();

        Racao entity = mapper.toEntity(request, lote, fornecedor);

        assertThat(entity).isNotNull();
        assertThat(entity.getLote()).isSameAs(lote);
        assertThat(entity.getFornecedor()).isSameAs(fornecedor);
        assertThat(entity.getDataAplicacao()).isEqualTo(request.getDataAplicacao());
        assertThat(entity.getTipoRacao()).isEqualTo(request.getTipoRacao());
        assertThat(entity.getMarca()).isEqualTo(request.getMarca());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getProteinaPercentual()).isEqualTo(request.getProteinaPercentual());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve atualizar Racao existente com dados do request")
    void deveAtualizarEntityComRequest() {
        Racao entity = criarEntity();
        RacaoRequest request = new RacaoRequest(
                30L,
                40L,
                LocalDate.of(2025, 2, 1),
                TipoRacaoEnum.ENGORDA,
                "Marca Y",
                new BigDecimal("50.0"),
                UnidadeMedidaEnum.KG,
                new BigDecimal("5.00"),
                new BigDecimal("40.0"),
                "Nova obs"
        );
        Lote novoLote = new Lote();
        novoLote.setId(30L);
        Fornecedor novoFornecedor = new Fornecedor();
        novoFornecedor.setId(40L);
        novoFornecedor.setNome("Fornecedor Y");

        mapper.updateEntity(entity, request, novoLote, novoFornecedor);

        assertThat(entity.getLote()).isSameAs(novoLote);
        assertThat(entity.getFornecedor()).isSameAs(novoFornecedor);
        assertThat(entity.getDataAplicacao()).isEqualTo(request.getDataAplicacao());
        assertThat(entity.getTipoRacao()).isEqualTo(request.getTipoRacao());
        assertThat(entity.getMarca()).isEqualTo(request.getMarca());
        assertThat(entity.getQuantidade()).isEqualTo(request.getQuantidade());
        assertThat(entity.getUnidade()).isEqualTo(request.getUnidade());
        assertThat(entity.getCustoUnitario()).isEqualTo(request.getCustoUnitario());
        assertThat(entity.getProteinaPercentual()).isEqualTo(request.getProteinaPercentual());
        assertThat(entity.getObservacoes()).isEqualTo(request.getObservacoes());
    }

    @Test
    @DisplayName("Deve converter Racao para RacaoResponse")
    void deveConverterEntityParaResponse() {
        Racao entity = criarEntity();

        RacaoResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getLoteId()).isEqualTo(entity.getLote().getId());
        assertThat(response.getFornecedorId()).isEqualTo(entity.getFornecedor().getId());
        assertThat(response.getDataAplicacao()).isEqualTo(entity.getDataAplicacao());
        assertThat(response.getTipoRacao()).isEqualTo(entity.getTipoRacao());
        assertThat(response.getMarca()).isEqualTo(entity.getMarca());
        assertThat(response.getQuantidade()).isEqualTo(entity.getQuantidade());
        assertThat(response.getUnidade()).isEqualTo(entity.getUnidade());
        assertThat(response.getCustoUnitario()).isEqualTo(entity.getCustoUnitario());
        assertThat(response.getCustoTotal()).isEqualTo(entity.getCustoTotal());
        assertThat(response.getProteinaPercentual()).isEqualTo(entity.getProteinaPercentual());
        assertThat(response.getObservacoes()).isEqualTo(entity.getObservacoes());
    }
}
