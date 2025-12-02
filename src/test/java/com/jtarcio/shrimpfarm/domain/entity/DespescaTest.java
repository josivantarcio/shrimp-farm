package com.jtarcio.shrimpfarm.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DespescaTest {

    @Test
    @DisplayName("Deve criar despesca com builder")
    void deveCriarDespescaComBuilder() {
        Lote lote = Lote.builder().id(1L).build();
        Comprador comprador = Comprador.builder().id(1L).build();

        Despesca despesca = Despesca.builder()
                .lote(lote)
                .comprador(comprador)
                .dataDespesca(LocalDate.of(2024, 12, 1))
                .pesoTotal(new BigDecimal("500.50"))
                .quantidadeDespescada(10000)
                .pesoMedioFinal(new BigDecimal("50.050"))
                .taxaSobrevivencia(new BigDecimal("85.50"))
                .precoVendaKg(new BigDecimal("35.00"))
                .custoDespesca(new BigDecimal("2000.00"))
                .observacoes("Ótima qualidade")
                .build();

        assertThat(despesca).isNotNull();
        assertThat(despesca.getLote()).isEqualTo(lote);
        assertThat(despesca.getComprador()).isEqualTo(comprador);
        assertThat(despesca.getPesoTotal()).isEqualByComparingTo("500.50");
        assertThat(despesca.getQuantidadeDespescada()).isEqualTo(10000);
    }

    @Test
    @DisplayName("Deve calcular receita total automaticamente no onCreate")
    void deveCalcularReceitaTotalNoOnCreate() {
        Despesca despesca = Despesca.builder()
                .pesoTotal(new BigDecimal("100.00"))
                .precoVendaKg(new BigDecimal("40.00"))
                .quantidadeDespescada(5000)
                .pesoMedioFinal(new BigDecimal("20.000"))
                .dataDespesca(LocalDate.now())
                .build();

        despesca.onCreate();

        assertThat(despesca.getReceitaTotal()).isEqualByComparingTo("4000.00");
        assertThat(despesca.getDataCriacao()).isNotNull();
        assertThat(despesca.getDataAtualizacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular receita total automaticamente no onUpdate")
    void deveCalcularReceitaTotalNoOnUpdate() throws InterruptedException {
        Despesca despesca = Despesca.builder()
                .pesoTotal(new BigDecimal("200.00"))
                .precoVendaKg(new BigDecimal("35.00"))
                .quantidadeDespescada(8000)
                .pesoMedioFinal(new BigDecimal("25.000"))
                .dataDespesca(LocalDate.now())
                .build();

        despesca.onCreate();
        LocalDateTime dataOriginal = despesca.getDataAtualizacao();

        Thread.sleep(10);

        // Atualizar preço
        despesca.setPrecoVendaKg(new BigDecimal("45.00"));
        despesca.onUpdate();

        assertThat(despesca.getReceitaTotal()).isEqualByComparingTo("9000.00");
        assertThat(despesca.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Não deve calcular receita se pesoTotal ou precoVendaKg for null")
    void naoDeveCalcularReceitaSeValoresNulos() {
        Despesca despesca1 = Despesca.builder()
                .pesoTotal(new BigDecimal("100.00"))
                .precoVendaKg(null)
                .build();

        Despesca despesca2 = Despesca.builder()
                .pesoTotal(null)
                .precoVendaKg(new BigDecimal("40.00"))
                .build();

        despesca1.onCreate();
        despesca2.onCreate();

        assertThat(despesca1.getReceitaTotal()).isNull();
        assertThat(despesca2.getReceitaTotal()).isNull();
    }

    @Test
    @DisplayName("Deve recalcular receita ao mudar peso total")
    void deveRecalcularReceitaAoMudarPesoTotal() {
        Despesca despesca = Despesca.builder()
                .pesoTotal(new BigDecimal("100.00"))
                .precoVendaKg(new BigDecimal("30.00"))
                .quantidadeDespescada(5000)
                .pesoMedioFinal(new BigDecimal("20.000"))
                .dataDespesca(LocalDate.now())
                .build();

        despesca.onCreate();
        assertThat(despesca.getReceitaTotal()).isEqualByComparingTo("3000.00");

        despesca.setPesoTotal(new BigDecimal("150.00"));
        despesca.onUpdate();

        assertThat(despesca.getReceitaTotal()).isEqualByComparingTo("4500.00");
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        Despesca despescaComObs = Despesca.builder()
                .observacoes("Camarões grandes e saudáveis")
                .dataDespesca(LocalDate.now())
                .pesoTotal(BigDecimal.TEN)
                .quantidadeDespescada(1000)
                .pesoMedioFinal(BigDecimal.TEN)
                .build();

        Despesca despescaSemObs = Despesca.builder()
                .dataDespesca(LocalDate.now())
                .pesoTotal(BigDecimal.TEN)
                .quantidadeDespescada(1000)
                .pesoMedioFinal(BigDecimal.TEN)
                .build();

        assertThat(despescaComObs.getObservacoes()).isEqualTo("Camarões grandes e saudáveis");
        assertThat(despescaSemObs.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar custo de despesca opcional")
    void deveAceitarCustoDespescaOpcional() {
        Despesca despesca = Despesca.builder()
                .custoDespesca(new BigDecimal("1500.00"))
                .pesoTotal(BigDecimal.TEN)
                .quantidadeDespescada(1000)
                .pesoMedioFinal(BigDecimal.TEN)
                .dataDespesca(LocalDate.now())
                .build();

        assertThat(despesca.getCustoDespesca()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Deve aceitar taxa de sobrevivência opcional")
    void deveAceitarTaxaSobrevivenciaOpcional() {
        Despesca despesca = Despesca.builder()
                .taxaSobrevivencia(new BigDecimal("92.50"))
                .pesoTotal(BigDecimal.TEN)
                .quantidadeDespescada(1000)
                .pesoMedioFinal(BigDecimal.TEN)
                .dataDespesca(LocalDate.now())
                .build();

        assertThat(despesca.getTaxaSobrevivencia()).isEqualByComparingTo("92.50");
    }

    @Test
    @DisplayName("Deve criar despesca com construtor padrão")
    void deveCriarDespescaComConstrutorPadrao() {
        Despesca despesca = new Despesca();
        despesca.setDataDespesca(LocalDate.of(2024, 12, 1));
        despesca.setPesoTotal(new BigDecimal("250.00"));
        despesca.setQuantidadeDespescada(7500);
        despesca.setPesoMedioFinal(new BigDecimal("33.333"));

        assertThat(despesca.getDataDespesca()).isEqualTo(LocalDate.of(2024, 12, 1));
        assertThat(despesca.getPesoTotal()).isEqualByComparingTo("250.00");
        assertThat(despesca.getQuantidadeDespescada()).isEqualTo(7500);
    }
}
