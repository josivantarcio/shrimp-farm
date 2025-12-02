package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoteTest {

    @Test
    @DisplayName("Deve criar lote com builder")
    void deveCriarLoteComBuilder() {
        Viveiro viveiro = Viveiro.builder().id(1L).build();

        Lote lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE01_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 15))
                .quantidadePosLarvas(500000)
                .custoPosLarvas(new BigDecimal("25000.00"))
                .densidadeInicial(new BigDecimal("50.00"))
                .status(StatusLoteEnum.ATIVO)
                .observacoes("Lote em condições ideais")
                .build();

        assertThat(lote).isNotNull();
        assertThat(lote.getViveiro()).isEqualTo(viveiro);
        assertThat(lote.getCodigo()).isEqualTo("LOTE01_2025");
        assertThat(lote.getDataPovoamento()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(lote.getQuantidadePosLarvas()).isEqualTo(500000);
        assertThat(lote.getCustoPosLarvas()).isEqualByComparingTo("25000.00");
        assertThat(lote.getDensidadeInicial()).isEqualByComparingTo("50.00");
        assertThat(lote.getStatus()).isEqualTo(StatusLoteEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar lote com construtor padrão")
    void deveCriarLoteComConstrutorPadrao() {
        Lote lote = new Lote();
        lote.setCodigo("LOTE02_2025");
        lote.setDataPovoamento(LocalDate.of(2025, 2, 1));
        lote.setQuantidadePosLarvas(400000);
        lote.setStatus(StatusLoteEnum.PLANEJADO);

        assertThat(lote.getCodigo()).isEqualTo("LOTE02_2025");
        assertThat(lote.getDataPovoamento()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(lote.getQuantidadePosLarvas()).isEqualTo(400000);
        assertThat(lote.getStatus()).isEqualTo(StatusLoteEnum.PLANEJADO);
    }

    @Test
    @DisplayName("Deve inicializar status como PLANEJADO por padrão")
    void deveInicializarStatusComoPlanejadoPorPadrao() {
        Lote lote = Lote.builder()
                .codigo("LOTE03_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        assertThat(lote.getStatus()).isEqualTo(StatusLoteEnum.PLANEJADO);
    }

    @Test
    @DisplayName("Deve inicializar listas vazias")
    void deveInicializarListasVazias() {
        Lote lote = Lote.builder()
                .codigo("LOTE04_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        assertThat(lote.getBiometrias()).isNotNull().isEmpty();
        assertThat(lote.getRacoes()).isNotNull().isEmpty();
        assertThat(lote.getNutrientes()).isNotNull().isEmpty();
        assertThat(lote.getFertilizacoes()).isNotNull().isEmpty();
        assertThat(lote.getCustosVariaveis()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve calcular dias de cultivo ao chamar onCreate")
    void deveCalcularDiasCultivoAoChamarOnCreate() {
        Lote lote = Lote.builder()
                .codigo("LOTE05_2025")
                .dataPovoamento(LocalDate.now().minusDays(30))
                .quantidadePosLarvas(400000)
                .build();

        lote.onCreate();

        assertThat(lote.getDiasCultivo()).isEqualTo(30);
    }

    @Test
    @DisplayName("Deve calcular dias de cultivo com data de despesca")
    void deveCalcularDiasCultivoComDataDespesca() {
        Lote lote = Lote.builder()
                .codigo("LOTE06_2025")
                .dataPovoamento(LocalDate.of(2025, 1, 1))
                .dataDespesca(LocalDate.of(2025, 4, 1))
                .quantidadePosLarvas(500000)
                .build();

        lote.onCreate();

        assertThat(lote.getDiasCultivo()).isEqualTo(90);
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Lote lote = Lote.builder()
                .codigo("LOTE07_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        lote.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(lote.getDataCriacao()).isNotNull();
        assertThat(lote.getDataAtualizacao()).isNotNull();
        assertThat(lote.getDataCriacao()).isBetween(antes, depois);
        assertThat(lote.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Lote lote = Lote.builder()
                .codigo("LOTE08_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        lote.onCreate();
        LocalDateTime dataOriginal = lote.getDataAtualizacao();

        Thread.sleep(10);

        lote.onUpdate();

        assertThat(lote.getDataAtualizacao()).isNotNull();
        assertThat(lote.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve adicionar biometria ao lote")
    void deveAdicionarBiometriaAoLote() {
        Lote lote = Lote.builder()
                .codigo("LOTE09_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        Biometria biometria = Biometria.builder().id(1L).build();

        lote.addBiometria(biometria);

        assertThat(lote.getBiometrias()).hasSize(1);
        assertThat(lote.getBiometrias()).contains(biometria);
        assertThat(biometria.getLote()).isEqualTo(lote);
    }

    @Test
    @DisplayName("Deve adicionar ração ao lote")
    void deveAdicionarRacaoAoLote() {
        Lote lote = Lote.builder()
                .codigo("LOTE10_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        Racao racao = Racao.builder().id(1L).build();

        lote.addRacao(racao);

        assertThat(lote.getRacoes()).hasSize(1);
        assertThat(lote.getRacoes()).contains(racao);
        assertThat(racao.getLote()).isEqualTo(lote);
    }

    @Test
    @DisplayName("Deve adicionar nutriente ao lote")
    void deveAdicionarNutrienteAoLote() {
        Lote lote = Lote.builder()
                .codigo("LOTE11_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        Nutriente nutriente = Nutriente.builder().id(1L).build();

        lote.addNutriente(nutriente);

        assertThat(lote.getNutrientes()).hasSize(1);
        assertThat(lote.getNutrientes()).contains(nutriente);
        assertThat(nutriente.getLote()).isEqualTo(lote);
    }

    @Test
    @DisplayName("Deve aceitar diferentes status")
    void deveAceitarDiferentesStatus() {
        Lote planejado = Lote.builder()
                .codigo("LOTE12_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .status(StatusLoteEnum.PLANEJADO)
                .build();

        Lote ativo = Lote.builder()
                .codigo("LOTE13_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .status(StatusLoteEnum.ATIVO)
                .build();

        Lote finalizado = Lote.builder()
                .codigo("LOTE14_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .status(StatusLoteEnum.FINALIZADO)
                .build();

        assertThat(planejado.getStatus()).isEqualTo(StatusLoteEnum.PLANEJADO);
        assertThat(ativo.getStatus()).isEqualTo(StatusLoteEnum.ATIVO);
        assertThat(finalizado.getStatus()).isEqualTo(StatusLoteEnum.FINALIZADO);
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisComoNull() {
        Lote lote = Lote.builder()
                .codigo("LOTE15_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .build();

        assertThat(lote.getDataDespesca()).isNull();
        assertThat(lote.getCustoPosLarvas()).isNull();
        assertThat(lote.getDensidadeInicial()).isNull();
        assertThat(lote.getObservacoes()).isNull();
        assertThat(lote.getDespesca()).isNull();
    }

    @Test
    @DisplayName("Deve associar lote a um viveiro")
    void deveAssociarLoteAUmViveiro() {
        Viveiro viveiro = Viveiro.builder().id(5L).build();

        Lote lote = Lote.builder()
                .viveiro(viveiro)
                .codigo("LOTE16_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(400000)
                .build();

        assertThat(lote.getViveiro()).isNotNull();
        assertThat(lote.getViveiro().getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Deve armazenar observações")
    void deveArmazenarObservacoes() {
        Lote lote = Lote.builder()
                .codigo("LOTE17_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(300000)
                .observacoes("Lote experimental com nova linhagem")
                .build();

        assertThat(lote.getObservacoes()).isEqualTo("Lote experimental com nova linhagem");
    }
}
