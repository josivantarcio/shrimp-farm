package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Lote")
class LoteTest {

    @Test
    @DisplayName("Deve criar Lote básico com builder")
    void deveCriarLoteBasicoComBuilder() {
        LocalDate dataPovoamento = LocalDate.of(2025, 1, 10);

        Lote lote = Lote.builder()
                .codigo("LOTE01_2025")
                .dataPovoamento(dataPovoamento)
                .quantidadePosLarvas(100_000)
                .custoPosLarvas(new BigDecimal("1500.00"))
                .densidadeInicial(new BigDecimal("50.00"))
                .status(StatusLoteEnum.ATIVO)
                .observacoes("Lote de teste")
                .build();

        assertNotNull(lote);
        assertEquals("LOTE01_2025", lote.getCodigo());
        assertEquals(dataPovoamento, lote.getDataPovoamento());
        assertEquals(100_000, lote.getQuantidadePosLarvas());
        assertEquals(new BigDecimal("1500.00"), lote.getCustoPosLarvas());
        assertEquals(new BigDecimal("50.00"), lote.getDensidadeInicial());
        assertEquals(StatusLoteEnum.ATIVO, lote.getStatus());
        assertEquals("Lote de teste", lote.getObservacoes());
    }

    @Test
    @DisplayName("Deve usar status PLANEJADO como padrão")
    void deveUsarStatusPlanejadoPorPadrao() {
        Lote lote = Lote.builder()
                .codigo("LOTE02_2025")
                .dataPovoamento(LocalDate.now())
                .quantidadePosLarvas(50_000)
                .build();

        assertEquals(StatusLoteEnum.PLANEJADO, lote.getStatus());
    }

    @Test
    @DisplayName("Deve calcular diasCultivo em onCreate sem dataDespesca")
    void deveCalcularDiasCultivoOnCreateSemDespesca() {
        LocalDate dataPovoamento = LocalDate.now().minusDays(7);

        Lote lote = Lote.builder()
                .codigo("LOTE03_2025")
                .dataPovoamento(dataPovoamento)
                .quantidadePosLarvas(80_000)
                .build();

        lote.onCreate(); // simula @PrePersist

        assertEquals(7, lote.getDiasCultivo());
        assertNotNull(lote.getDataCriacao());
        assertNotNull(lote.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve calcular diasCultivo em onCreate com dataDespesca")
    void deveCalcularDiasCultivoOnCreateComDespesca() {
        LocalDate dataPovoamento = LocalDate.of(2025, 1, 1);
        LocalDate dataDespesca = LocalDate.of(2025, 1, 31);

        Lote lote = Lote.builder()
                .codigo("LOTE04_2025")
                .dataPovoamento(dataPovoamento)
                .dataDespesca(dataDespesca)
                .quantidadePosLarvas(90_000)
                .build();

        lote.onCreate(); // simula @PrePersist

        assertEquals(30, lote.getDiasCultivo()); // 31 - 1 = 30 dias
    }
}
