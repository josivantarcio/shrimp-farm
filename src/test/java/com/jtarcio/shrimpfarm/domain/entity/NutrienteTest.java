package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.TipoNutrienteEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NutrienteTest {

    @Test
    @DisplayName("Deve criar nutriente com builder")
    void deveCriarNutrienteComBuilder() {
        Lote lote = Lote.builder().id(1L).build();
        Fornecedor fornecedor = Fornecedor.builder().id(10L).build();

        Nutriente nutriente = Nutriente.builder()
                .lote(lote)
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.of(2024, 12, 1))
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário Dolomítico")
                .quantidade(new BigDecimal("500.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("0.50"))
                .custoTotal(new BigDecimal("250.00"))
                .observacoes("Aplicação para correção de pH")
                .build();

        assertThat(nutriente).isNotNull();
        assertThat(nutriente.getLote()).isEqualTo(lote);
        assertThat(nutriente.getFornecedor()).isEqualTo(fornecedor);
        assertThat(nutriente.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.MINERAL);
        assertThat(nutriente.getProduto()).isEqualTo("Calcário Dolomítico");
        assertThat(nutriente.getQuantidade()).isEqualByComparingTo("500.000");
        assertThat(nutriente.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
    }

    @Test
    @DisplayName("Deve criar nutriente com construtor padrão")
    void deveCriarNutrienteComConstrutorPadrao() {
        Nutriente nutriente = new Nutriente();
        nutriente.setDataAplicacao(LocalDate.of(2024, 12, 15));
        nutriente.setTipoNutriente(TipoNutrienteEnum.VITAMINA);
        nutriente.setProduto("Complexo Vitamínico");
        nutriente.setQuantidade(new BigDecimal("100.000"));
        nutriente.setUnidade(UnidadeMedidaEnum.KG);

        assertThat(nutriente.getDataAplicacao()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(nutriente.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.VITAMINA);
        assertThat(nutriente.getProduto()).isEqualTo("Complexo Vitamínico");
    }

    @Test
    @DisplayName("Deve calcular custo total ao chamar onCreate")
    void deveCalcularCustoTotalAoChamarOnCreate() {
        Nutriente nutriente = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.PROBIOTICO)
                .produto("Probiótico Premium")
                .quantidade(new BigDecimal("200.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("1.50"))
                .build();

        nutriente.onCreate();

        assertThat(nutriente.getCustoTotal()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Deve calcular custo total ao chamar onUpdate")
    void deveCalcularCustoTotalAoChamarOnUpdate() {
        Nutriente nutriente = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(new BigDecimal("300.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("0.80"))
                .build();

        nutriente.onUpdate();

        assertThat(nutriente.getCustoTotal()).isEqualByComparingTo("240.00");
    }

    @Test
    @DisplayName("Deve recalcular custo total quando quantidade ou custo unitário mudarem")
    void deveRecalcularCustoTotalQuandoValoresMudarem() {
        Nutriente nutriente = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.VITAMINA)
                .produto("Vitaminas")
                .quantidade(new BigDecimal("50.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("2.00"))
                .build();

        nutriente.onCreate();
        assertThat(nutriente.getCustoTotal()).isEqualByComparingTo("100.00");

        // Alterar quantidade
        nutriente.setQuantidade(new BigDecimal("100.000"));
        nutriente.onUpdate();
        assertThat(nutriente.getCustoTotal()).isEqualByComparingTo("200.00");

        // Alterar custo unitário
        nutriente.setCustoUnitario(new BigDecimal("3.00"));
        nutriente.onUpdate();
        assertThat(nutriente.getCustoTotal()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Não deve calcular custo total se quantidade ou custo unitário forem null")
    void naoDeveCalcularCustoTotalSeValoresForemNull() {
        Nutriente nutriente1 = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(null)
                .build();

        Nutriente nutriente2 = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.VITAMINA)
                .produto("Vitamina C")
                .quantidade(null)
                .unidade(UnidadeMedidaEnum.KG)
                .custoUnitario(new BigDecimal("2.00"))
                .build();

        nutriente1.onCreate();
        nutriente2.onCreate();

        assertThat(nutriente1.getCustoTotal()).isNull();
        assertThat(nutriente2.getCustoTotal()).isNull();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Nutriente nutriente = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.IMUNOESTIMULANTE)
                .produto("Imunoestimulante Premium")
                .quantidade(new BigDecimal("150.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        nutriente.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(nutriente.getDataCriacao()).isNotNull();
        assertThat(nutriente.getDataAtualizacao()).isNotNull();
        assertThat(nutriente.getDataCriacao()).isBetween(antes, depois);
        assertThat(nutriente.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Nutriente nutriente = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(new BigDecimal("200.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        nutriente.onCreate();
        LocalDateTime dataOriginal = nutriente.getDataAtualizacao();

        Thread.sleep(10);

        nutriente.onUpdate();

        assertThat(nutriente.getDataAtualizacao()).isNotNull();
        assertThat(nutriente.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve aceitar diferentes tipos de nutriente")
    void deveAceitarDiferentesTiposDeNutriente() {
        Nutriente probiotico = Nutriente.builder()
                .tipoNutriente(TipoNutrienteEnum.PROBIOTICO)
                .dataAplicacao(LocalDate.now())
                .produto("Probiótico")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Nutriente vitamina = Nutriente.builder()
                .tipoNutriente(TipoNutrienteEnum.VITAMINA)
                .dataAplicacao(LocalDate.now())
                .produto("Vitamina")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Nutriente mineral = Nutriente.builder()
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .dataAplicacao(LocalDate.now())
                .produto("Mineral")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Nutriente imunoestimulante = Nutriente.builder()
                .tipoNutriente(TipoNutrienteEnum.IMUNOESTIMULANTE)
                .dataAplicacao(LocalDate.now())
                .produto("Imunoestimulante")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Nutriente melhoradorAgua = Nutriente.builder()
                .tipoNutriente(TipoNutrienteEnum.MELHORADOR_AGUA)
                .dataAplicacao(LocalDate.now())
                .produto("Melhorador de Água")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(probiotico.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.PROBIOTICO);
        assertThat(vitamina.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.VITAMINA);
        assertThat(mineral.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.MINERAL);
        assertThat(imunoestimulante.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.IMUNOESTIMULANTE);
        assertThat(melhoradorAgua.getTipoNutriente()).isEqualTo(TipoNutrienteEnum.MELHORADOR_AGUA);
    }

    @Test
    @DisplayName("Deve aceitar diferentes unidades de medida")
    void deveAceitarDiferentesUnidadesDeMedida() {
        Nutriente nutrienteKg = Nutriente.builder()
                .unidade(UnidadeMedidaEnum.KG)
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(BigDecimal.TEN)
                .build();

        Nutriente nutrienteLitros = Nutriente.builder()
                .unidade(UnidadeMedidaEnum.LITRO)
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.PROBIOTICO)
                .produto("Probiótico Líquido")
                .quantidade(BigDecimal.TEN)
                .build();

        assertThat(nutrienteKg.getUnidade()).isEqualTo(UnidadeMedidaEnum.KG);
        assertThat(nutrienteLitros.getUnidade()).isEqualTo(UnidadeMedidaEnum.LITRO);
    }

    @Test
    @DisplayName("Deve associar nutriente a um lote")
    void deveAssociarNutrienteAUmLote() {
        Lote lote = Lote.builder().id(5L).build();

        Nutriente nutriente = Nutriente.builder()
                .lote(lote)
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(new BigDecimal("100.000"))
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(nutriente.getLote()).isNotNull();
        assertThat(nutriente.getLote().getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Deve aceitar fornecedor opcional")
    void deveAceitarFornecedorOpcional() {
        Fornecedor fornecedor = Fornecedor.builder().id(3L).build();

        Nutriente comFornecedor = Nutriente.builder()
                .fornecedor(fornecedor)
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.VITAMINA)
                .produto("Vitaminas")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        Nutriente semFornecedor = Nutriente.builder()
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MINERAL)
                .produto("Calcário")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.KG)
                .build();

        assertThat(comFornecedor.getFornecedor()).isNotNull();
        assertThat(comFornecedor.getFornecedor().getId()).isEqualTo(3L);
        assertThat(semFornecedor.getFornecedor()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        Nutriente nutriente = Nutriente.builder()
                .observacoes("Aplicação em horário de menor temperatura")
                .dataAplicacao(LocalDate.now())
                .tipoNutriente(TipoNutrienteEnum.MELHORADOR_AGUA)
                .produto("Melhorador de Água")
                .quantidade(BigDecimal.TEN)
                .unidade(UnidadeMedidaEnum.LITRO)
                .build();

        assertThat(nutriente.getObservacoes()).isEqualTo("Aplicação em horário de menor temperatura");
    }
}
