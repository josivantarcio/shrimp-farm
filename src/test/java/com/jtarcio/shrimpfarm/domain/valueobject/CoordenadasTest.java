package com.jtarcio.shrimpfarm.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordenadasTest {

    @Test
    @DisplayName("Deve criar coordenadas vazias com construtor padrão")
    void deveCriarCoordenadasVazias() {
        Coordenadas coordenadas = new Coordenadas();

        assertThat(coordenadas.getLatitude()).isNull();
        assertThat(coordenadas.getLongitude()).isNull();
    }

    @Test
    @DisplayName("Deve criar coordenadas com valores")
    void deveCriarCoordenadasComValores() {
        BigDecimal lat = new BigDecimal("-5.795400");
        BigDecimal lon = new BigDecimal("-35.209000");

        Coordenadas coordenadas = new Coordenadas(lat, lon);

        assertThat(coordenadas.getLatitude()).isEqualTo(lat);
        assertThat(coordenadas.getLongitude()).isEqualTo(lon);
    }

    @Test
    @DisplayName("Deve validar coordenadas válidas")
    void deveValidarCoordenadasValidas() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-5.795400"),
                new BigDecimal("-35.209000")
        );

        assertThat(coordenadas.isValida()).isTrue();
    }

    @Test
    @DisplayName("Deve validar coordenadas nos limites mínimos")
    void deveValidarCoordenadasNosLimitesMinimos() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-90"),
                new BigDecimal("-180")
        );

        assertThat(coordenadas.isValida()).isTrue();
    }

    @Test
    @DisplayName("Deve validar coordenadas nos limites máximos")
    void deveValidarCoordenadasNosLimitesMaximos() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("90"),
                new BigDecimal("180")
        );

        assertThat(coordenadas.isValida()).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar latitude maior que 90")
    void deveRejeitarLatitudeMaiorQue90() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("91"),
                new BigDecimal("0")
        );

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar latitude menor que -90")
    void deveRejeitarLatitudeMenorQueNegativo90() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-91"),
                new BigDecimal("0")
        );

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar longitude maior que 180")
    void deveRejeitarLongitudeMaiorQue180() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("0"),
                new BigDecimal("181")
        );

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar longitude menor que -180")
    void deveRejeitarLongitudeMenorQueNegativo180() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("0"),
                new BigDecimal("-181")
        );

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar coordenadas com latitude null")
    void deveRejeitarCoordenadasComLatitudeNull() {
        Coordenadas coordenadas = new Coordenadas(null, new BigDecimal("0"));

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar coordenadas com longitude null")
    void deveRejeitarCoordenadasComLongitudeNull() {
        Coordenadas coordenadas = new Coordenadas(new BigDecimal("0"), null);

        assertThat(coordenadas.isValida()).isFalse();
    }

    @Test
    @DisplayName("Deve formatar coordenadas para Maps")
    void deveFormatarCoordenadasParaMaps() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-5.795400"),
                new BigDecimal("-35.209000")
        );

        assertThat(coordenadas.formatarParaMaps()).isEqualTo("-5.795400,-35.209000");
    }

    @Test
    @DisplayName("Deve lançar exceção ao formatar coordenadas inválidas")
    void deveLancarExcecaoAoFormatarCoordenadasInvalidas() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("91"),
                new BigDecimal("0")
        );

        assertThatThrownBy(() -> coordenadas.formatarParaMaps())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Coordenadas inválidas");
    }

    @Test
    @DisplayName("Deve gerar URL do Google Maps")
    void deveGerarUrlDoGoogleMaps() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-5.795400"),
                new BigDecimal("-35.209000")
        );

        assertThat(coordenadas.getUrlGoogleMaps())
                .isEqualTo("https://www.google.com/maps?q=-5.795400,-35.209000");
    }

    @Test
    @DisplayName("Deve retornar string formatada no toString")
    void deveRetornarStringFormatadaNoToString() {
        Coordenadas coordenadas = new Coordenadas(
                new BigDecimal("-5.795400"),
                new BigDecimal("-35.209000")
        );

        assertThat(coordenadas.toString())
                .isEqualTo("Lat: -5.795400, Long: -35.209000");
    }
}
