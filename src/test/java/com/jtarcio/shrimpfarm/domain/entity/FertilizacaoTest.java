package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FertilizacaoTest {

    @Test
    @DisplayName("Deve criar fertilização com builder")
    void deveCriarFertilizacaoComBuilder() {
        Lote lote = Lote.builder().id(1L).build();
        Fornecedor fornecedor = Fornecedor.builder().id(1L).build();

        Fertilizacao fertilizacao = Fertilizacao.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2024, 12, 1))
                .produto("Ureia")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("5.50"))
                .finalidade("Preparação do viveiro")
                .observacoes("Aplicado uniformemente")
                .build();

        assertThat(fertilizacao).isNotNull();
        assertThat(fertilizacao.getLote()).isEqualTo(lote);
        assertThat(fertilizacao.getFornecedor()).isEqualTo(fornecedor);
        assertThat(fertilizacao.getProduto()).isEqualTo("Ureia");
        assertThat(fertilizacao.getQuantidade()).isEqualByComparingTo("100.000");
        assertThat(fertilizacao.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
    }

    @Test
    @DisplayName("Deve calcular custo total automaticamente no onCreate")
    void deveCalcularCustoTotalNoOnCreate() {
        Fertilizacao fertilizacao = Fertilizacao.builder()
                .produto("Calcário")
                .quantidade(new BigDecimal("200.000"))
                .custoUnitario(new BigDecimal("2.50"))
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        fertilizacao.onCreate();

        assertThat(fertilizacao.getCustoTotal()).isEqualByComparingTo("500.00");
        assertThat(fertilizacao.getDataCriacao()).isNotNull();
        assertThat(fertilizacao.getDataAtualizacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular custo total automaticamente no onUpdate")
    void deveCalcularCustoTotalNoOnUpdate() throws InterruptedException {
        Fertilizacao fertilizacao = Fertilizacao.builder()
                .produto("Superfosfato")
                .quantidade(new BigDecimal("50.000"))
                .custoUnitario(new BigDecimal("8.00"))
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        fertilizacao.onCreate();
        assertThat(fertilizacao.getCustoTotal()).isEqualByComparingTo("400.00");

        Thread.sleep(10);

        // Atualizar custo unitário
        fertilizacao.setCustoUnitario(new BigDecimal("10.00"));
        fertilizacao.onUpdate();

        assertThat(fertilizacao.getCustoTotal()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("Não deve calcular custo total se quantidade ou custoUnitario for null")
    void naoDeveCalcularCustoTotalSeValoresNulos() {
        Fertilizacao fertilizacao1 = Fertilizacao.builder()
                .quantidade(new BigDecimal("100.000"))
                .custoUnitario(null)
                .produto("Teste")
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        Fertilizacao fertilizacao2 = Fertilizacao.builder()
                .quantidade(null)
                .custoUnitario(new BigDecimal("5.00"))
                .produto("Teste")
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        fertilizacao1.onCreate();
        fertilizacao2.onCreate();

        assertThat(fertilizacao1.getCustoTotal()).isNull();
        assertThat(fertilizacao2.getCustoTotal()).isNull();
    }

    @Test
    @DisplayName("Deve recalcular custo total ao mudar quantidade")
    void deveRecalcularCustoTotalAoMudarQuantidade() {
        Fertilizacao fertilizacao = Fertilizacao.builder()
                .produto("NPK")
                .quantidade(new BigDecimal("30.000"))
                .custoUnitario(new BigDecimal("12.00"))
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        fertilizacao.onCreate();
        assertThat(fertilizacao.getCustoTotal()).isEqualByComparingTo("360.00");

        fertilizacao.setQuantidade(new BigDecimal("40.000"));
        fertilizacao.onUpdate();

        assertThat(fertilizacao.getCustoTotal()).isEqualByComparingTo("480.00");
    }

    @Test
    @DisplayName("Deve criar fertilização com construtor padrão")
    void deveCriarFertilizacaoComConstrutorPadrao() {
        Fertilizacao fertilizacao = new Fertilizacao();
        fertilizacao.setProduto("Adubo Orgânico");
        fertilizacao.setQuantidade(new BigDecimal("150.000"));
        fertilizacao.setUnidade(UnidadeMedidaEnum.KG);
        fertilizacao.setDataAplicacao(LocalDate.of(2024, 11, 15));

        assertThat(fertilizacao.getProduto()).isEqualTo("Adubo Orgânico");
        assertThat(fertilizacao.getQuantidade()).isEqualByComparingTo("150.000");
        assertThat(fertilizacao.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
    }

    @Test
    @DisplayName("Deve aceitar diferentes unidades de medida")
    void deveAceitarDiferentesUnidadesDeMedida() {
        Fertilizacao fertilKg = Fertilizacao.builder()
                .unidade(UnidadeMedidaEnum.KG)
                .produto("Teste KG")
                .quantidade(BigDecimal.ONE)
                .dataAplicacao(LocalDate.now())
                .build();

        Fertilizacao fertilLitro = Fertilizacao.builder()
                .unidade(UnidadeMedidaEnum.LITRO)
                .produto("Teste L")
                .quantidade(BigDecimal.ONE)
                .dataAplicacao(LocalDate.now())
                .build();

        assertThat(fertilKg.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
        assertThat(fertilLitro.getUnidade()).isEqualTo(UnidadeMedidaEnum.LITRO);
    }

    @Test
    @DisplayName("Deve armazenar finalidade opcional")
    void deveArmazenarFinalidadeOpcional() {
        Fertilizacao fertilComFinalidade = Fertilizacao.builder()
                .finalidade("Manutenção")
                .produto("Teste")
                .quantidade(BigDecimal.ONE)
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        Fertilizacao fertilSemFinalidade = Fertilizacao.builder()
                .produto("Teste")
                .quantidade(BigDecimal.ONE)
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        assertThat(fertilComFinalidade.getFinalidade()).isEqualTo("Manutenção");
        assertThat(fertilSemFinalidade.getFinalidade()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        Fertilizacao fertilizacao = Fertilizacao.builder()
                .observacoes("Aplicado em condições ideais")
                .produto("Teste")
                .quantidade(BigDecimal.ONE)
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        assertThat(fertilizacao.getObservacoes()).isEqualTo("Aplicado em condições ideais");
    }

    @Test
    @DisplayName("Deve aceitar fornecedor opcional")
    void deveAceitarFornecedorOpcional() {
        Fornecedor fornecedor = Fornecedor.builder().id(5L).build();

        Fertilizacao fertilComFornecedor = Fertilizacao.builder()
                .fornecedor(fornecedor)
                .produto("Teste")
                .quantidade(BigDecimal.ONE)
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        Fertilizacao fertilSemFornecedor = Fertilizacao.builder()
                .produto("Teste")
                .quantidade(BigDecimal.ONE)
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        assertThat(fertilComFornecedor.getFornecedor()).isNotNull();
        assertThat(fertilComFornecedor.getFornecedor().getId()).isEqualTo(5L);
        assertThat(fertilSemFornecedor.getFornecedor()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao sem alterar dataCriacao")
    void deveAtualizarDataAtualizacaoSemAlterarDataCriacao() throws InterruptedException {
        Fertilizacao fertilizacao = Fertilizacao.builder()
                .produto("Teste")
                .quantidade(new BigDecimal("10.000"))
                .custoUnitario(new BigDecimal("5.00"))
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .build();

        fertilizacao.onCreate();
        LocalDateTime dataCriacaoOriginal = fertilizacao.getDataCriacao();

        Thread.sleep(10);

        fertilizacao.onUpdate();

        assertThat(fertilizacao.getDataCriacao()).isEqualTo(dataCriacaoOriginal);
        assertThat(fertilizacao.getDataAtualizacao()).isAfter(dataCriacaoOriginal);
    }
}
