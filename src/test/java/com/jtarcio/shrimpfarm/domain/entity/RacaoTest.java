package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.TipoRacaoEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RacaoTest {

    @Test
    @DisplayName("Deve criar ração com builder")
    void deveCriarRacaoComBuilder() {
        Lote lote = Lote.builder().id(1L).build();
        Fornecedor fornecedor = Fornecedor.builder().id(5L).build();

        Racao racao = Racao.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2024, 12, 1))
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(new BigDecimal("50.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("8.50"))
                .custoTotal(new BigDecimal("425.00"))
                .proteinaPercentual(new BigDecimal("35.00"))
                .observacoes("Ração de alta qualidade")
                .build();

        assertThat(racao).isNotNull();
        assertThat(racao.getLote()).isEqualTo(lote);
        assertThat(racao.getFornecedor()).isEqualTo(fornecedor);
        assertThat(racao.getTipoRacao()).isEqualTo(TipoRacaoEnum.CRESCIMENTO);
        assertThat(racao.getMarca()).isEqualTo("Guabi");
        assertThat(racao.getQuantidade()).isEqualByComparingTo("50.000");
        assertThat(racao.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
        assertThat(racao.getProteinaPercentual()).isEqualByComparingTo("35.00");
    }

    @Test
    @DisplayName("Deve criar ração com construtor padrão")
    void deveCriarRacaoComConstrutorPadrao() {
        Racao racao = new Racao();
        racao.setDataAplicacao(LocalDate.of(2024, 12, 15));
        racao.setTipoRacao(TipoRacaoEnum.ENGORDA);
        racao.setMarca("Presence");
        racao.setQuantidade(new BigDecimal("100.000"));
        racao.setUnidade(UnidadeMedidaEnum.KG);

        assertThat(racao.getDataAplicacao()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(racao.getTipoRacao()).isEqualTo(TipoRacaoEnum.ENGORDA);
        assertThat(racao.getMarca()).isEqualTo("Presence");
    }

    @Test
    @DisplayName("Deve calcular custo total ao chamar onCreate")
    void deveCalcularCustoTotalAoChamarOnCreate() {
        Racao racao = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(new BigDecimal("75.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("10.00"))
                .build();

        racao.onCreate();

        assertThat(racao.getCustoTotal()).isEqualByComparingTo("750.00");
    }

    @Test
    @DisplayName("Deve calcular custo total ao chamar onUpdate")
    void deveCalcularCustoTotalAoChamarOnUpdate() {
        Racao racao = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Presence")
                .quantidade(new BigDecimal("200.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("7.50"))
                .build();

        racao.onUpdate();

        assertThat(racao.getCustoTotal()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Deve recalcular custo total quando quantidade ou custo unitário mudarem")
    void deveRecalcularCustoTotalQuandoValoresMudarem() {
        Racao racao = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .marca("Guabi")
                .quantidade(new BigDecimal("25.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("12.00"))
                .build();

        racao.onCreate();
        assertThat(racao.getCustoTotal()).isEqualByComparingTo("300.00");

        // Alterar quantidade
        racao.setQuantidade(new BigDecimal("50.000"));
        racao.onUpdate();
        assertThat(racao.getCustoTotal()).isEqualByComparingTo("600.00");

        // Alterar custo unitário
        racao.setCustoUnitario(new BigDecimal("15.00"));
        racao.onUpdate();
        assertThat(racao.getCustoTotal()).isEqualByComparingTo("750.00");
    }

    @Test
    @DisplayName("Não deve calcular custo total se quantidade ou custo unitário forem null")
    void naoDeveCalcularCustoTotalSeValoresForemNull() {
        Racao racao1 = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(null)
                .build();

        Racao racao2 = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Presence")
                .quantidade(null)
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("10.00"))
                .build();

        racao1.onCreate();
        racao2.onCreate();

        assertThat(racao1.getCustoTotal()).isNull();
        assertThat(racao2.getCustoTotal()).isNull();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Racao racao = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(new BigDecimal("50.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        racao.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(racao.getDataCriacao()).isNotNull();
        assertThat(racao.getDataAtualizacao()).isNotNull();
        assertThat(racao.getDataCriacao()).isBetween(antes, depois);
        assertThat(racao.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Racao racao = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Presence")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        racao.onCreate();
        LocalDateTime dataOriginal = racao.getDataAtualizacao();

        Thread.sleep(10);

        racao.onUpdate();

        assertThat(racao.getDataAtualizacao()).isNotNull();
        assertThat(racao.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve aceitar diferentes tipos de ração")
    void deveAceitarDiferentesTiposDeRacao() {
        Racao inicial = Racao.builder()
                .tipoRacao(TipoRacaoEnum.INICIAL)
                .dataAplicacao(LocalDate.now())
                .marca("Guabi")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Racao crescimento = Racao.builder()
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .dataAplicacao(LocalDate.now())
                .marca("Presence")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Racao engorda = Racao.builder()
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .dataAplicacao(LocalDate.now())
                .marca("Guabi")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(inicial.getTipoRacao()).isEqualTo(TipoRacaoEnum.INICIAL);
        assertThat(crescimento.getTipoRacao()).isEqualTo(TipoRacaoEnum.CRESCIMENTO);
        assertThat(engorda.getTipoRacao()).isEqualTo(TipoRacaoEnum.ENGORDA);
    }

    @Test
    @DisplayName("Deve aceitar diferentes unidades de medida")
    void deveAceitarDiferentesUnidadesDeMedida() {
        Racao racaoKg = Racao.builder()
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(BigDecimal.TEN)
                .build();

        assertThat(racaoKg.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
    }

    @Test
    @DisplayName("Deve associar ração a um lote")
    void deveAssociarRacaoAUmLote() {
        Lote lote = Lote.builder().id(3L).build();

        Racao racao = Racao.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(new BigDecimal("50.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(racao.getLote()).isNotNull();
        assertThat(racao.getLote().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve aceitar fornecedor opcional")
    void deveAceitarFornecedorOpcional() {
        Fornecedor fornecedor = Fornecedor.builder().id(7L).build();

        Racao comFornecedor = Racao.builder()
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Presence")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Racao semFornecedor = Racao.builder()
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.CRESCIMENTO)
                .marca("Guabi")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(comFornecedor.getFornecedor()).isNotNull();
        assertThat(comFornecedor.getFornecedor().getId()).isEqualTo(7L);
        assertThat(semFornecedor.getFornecedor()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar percentual de proteína opcional")
    void deveArmazenarPercentualDeProteinaOpcional() {
        Racao racao = Racao.builder()
                .proteinaPercentual(new BigDecimal("38.50"))
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Presence")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(racao.getProteinaPercentual()).isEqualByComparingTo("38.50");
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        Racao racao = Racao.builder()
                .observacoes("Ração especial para fase final")
                .dataAplicacao(LocalDate.now())
                .tipoRacao(TipoRacaoEnum.ENGORDA)
                .marca("Guabi")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(racao.getObservacoes()).isEqualTo("Ração especial para fase final");
    }
}
