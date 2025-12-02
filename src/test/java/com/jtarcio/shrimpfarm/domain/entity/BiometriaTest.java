package com.jtarcio.shrimpfarm.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BiometriaTest {

    @Test
    @DisplayName("Deve criar biometria com builder")
    void deveCriarBiometriaComBuilder() {
        Lote lote = Lote.builder().id(1L).build();

        Biometria biometria = Biometria.builder()
                .lote(lote)
                .dataBiometria(LocalDate.of(2024, 12, 1))
                .diaCultivo(30)
                .pesoMedio(new BigDecimal("15.500"))
                .quantidadeAmostrada(100)
                .pesoTotalAmostra(new BigDecimal("1550.000"))
                .ganhoPesoDiario(new BigDecimal("0.5167"))
                .biomassaEstimada(new BigDecimal("775.00"))
                .sobrevivenciaEstimada(new BigDecimal("85.00"))
                .fatorConversaoAlimentar(new BigDecimal("1.250"))
                .observacoes("Boa taxa de crescimento")
                .build();

        assertThat(biometria).isNotNull();
        assertThat(biometria.getLote()).isEqualTo(lote);
        assertThat(biometria.getDiaCultivo()).isEqualTo(30);
        assertThat(biometria.getPesoMedio()).isEqualByComparingTo("15.500");
        assertThat(biometria.getQuantidadeAmostrada()).isEqualTo(100);
    }

    @Test
    @DisplayName("Deve criar biometria com construtor padrão")
    void deveCriarBiometriaComConstrutorPadrao() {
        Biometria biometria = new Biometria();
        biometria.setDataBiometria(LocalDate.of(2024, 12, 15));
        biometria.setDiaCultivo(45);
        biometria.setPesoMedio(new BigDecimal("20.000"));
        biometria.setQuantidadeAmostrada(50);

        assertThat(biometria.getDataBiometria()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(biometria.getDiaCultivo()).isEqualTo(45);
        assertThat(biometria.getPesoMedio()).isEqualByComparingTo("20.000");
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Biometria biometria = Biometria.builder()
                .dataBiometria(LocalDate.now())
                .diaCultivo(20)
                .pesoMedio(new BigDecimal("10.000"))
                .quantidadeAmostrada(100)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        biometria.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(biometria.getDataCriacao()).isNotNull();
        assertThat(biometria.getDataAtualizacao()).isNotNull();
        assertThat(biometria.getDataCriacao()).isBetween(antes, depois);
        assertThat(biometria.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Biometria biometria = Biometria.builder()
                .dataBiometria(LocalDate.now())
                .diaCultivo(25)
                .pesoMedio(new BigDecimal("12.000"))
                .quantidadeAmostrada(80)
                .build();

        biometria.onCreate();
        LocalDateTime dataOriginal = biometria.getDataAtualizacao();

        Thread.sleep(10);

        biometria.onUpdate();

        assertThat(biometria.getDataAtualizacao()).isNotNull();
        assertThat(biometria.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve armazenar campos calculados opcionais")
    void deveArmazenarCamposCalculadosOpcionais() {
        Biometria biometria = Biometria.builder()
                .dataBiometria(LocalDate.now())
                .diaCultivo(40)
                .pesoMedio(new BigDecimal("18.500"))
                .quantidadeAmostrada(120)
                .ganhoPesoDiario(new BigDecimal("0.4625"))
                .biomassaEstimada(new BigDecimal("925.00"))
                .sobrevivenciaEstimada(new BigDecimal("90.00"))
                .fatorConversaoAlimentar(new BigDecimal("1.150"))
                .build();

        assertThat(biometria.getGanhoPesoDiario()).isEqualByComparingTo("0.4625");
        assertThat(biometria.getBiomassaEstimada()).isEqualByComparingTo("925.00");
        assertThat(biometria.getSobrevivenciaEstimada()).isEqualByComparingTo("90.00");
        assertThat(biometria.getFatorConversaoAlimentar()).isEqualByComparingTo("1.150");
    }

    @Test
    @DisplayName("Deve aceitar peso total da amostra opcional")
    void deveAceitarPesoTotalAmostraOpcional() {
        Biometria biometriaComPesoTotal = Biometria.builder()
                .pesoTotalAmostra(new BigDecimal("2000.000"))
                .dataBiometria(LocalDate.now())
                .diaCultivo(15)
                .pesoMedio(new BigDecimal("10.000"))
                .quantidadeAmostrada(200)
                .build();

        Biometria biometriaSemPesoTotal = Biometria.builder()
                .dataBiometria(LocalDate.now())
                .diaCultivo(15)
                .pesoMedio(new BigDecimal("10.000"))
                .quantidadeAmostrada(200)
                .build();

        assertThat(biometriaComPesoTotal.getPesoTotalAmostra()).isEqualByComparingTo("2000.000");
        assertThat(biometriaSemPesoTotal.getPesoTotalAmostra()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        Biometria biometria = Biometria.builder()
                .observacoes("Crescimento uniforme, sem variações significativas")
                .dataBiometria(LocalDate.now())
                .diaCultivo(35)
                .pesoMedio(new BigDecimal("16.000"))
                .quantidadeAmostrada(90)
                .build();

        assertThat(biometria.getObservacoes()).isEqualTo("Crescimento uniforme, sem variações significativas");
    }

    @Test
    @DisplayName("Deve aceitar diferentes dias de cultivo")
    void deveAceitarDiferentesDiasDeCultivo() {
        Biometria bio1 = Biometria.builder()
                .diaCultivo(10)
                .dataBiometria(LocalDate.now())
                .pesoMedio(BigDecimal.TEN)
                .quantidadeAmostrada(100)
                .build();

        Biometria bio2 = Biometria.builder()
                .diaCultivo(60)
                .dataBiometria(LocalDate.now())
                .pesoMedio(BigDecimal.TEN)
                .quantidadeAmostrada(100)
                .build();

        assertThat(bio1.getDiaCultivo()).isEqualTo(10);
        assertThat(bio2.getDiaCultivo()).isEqualTo(60);
    }

    @Test
    @DisplayName("Deve associar biometria a um lote")
    void deveAssociarBiometriaAUmLote() {
        Lote lote = Lote.builder().id(5L).build();

        Biometria biometria = Biometria.builder()
                .lote(lote)
                .dataBiometria(LocalDate.now())
                .diaCultivo(28)
                .pesoMedio(new BigDecimal("14.000"))
                .quantidadeAmostrada(75)
                .build();

        assertThat(biometria.getLote()).isNotNull();
        assertThat(biometria.getLote().getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Deve aceitar campos calculados como null")
    void deveAceitarCamposCalculadosComoNull() {
        Biometria biometria = Biometria.builder()
                .dataBiometria(LocalDate.now())
                .diaCultivo(20)
                .pesoMedio(new BigDecimal("12.000"))
                .quantidadeAmostrada(100)
                .build();

        assertThat(biometria.getGanhoPesoDiario()).isNull();
        assertThat(biometria.getBiomassaEstimada()).isNull();
        assertThat(biometria.getSobrevivenciaEstimada()).isNull();
        assertThat(biometria.getFatorConversaoAlimentar()).isNull();
    }
}
