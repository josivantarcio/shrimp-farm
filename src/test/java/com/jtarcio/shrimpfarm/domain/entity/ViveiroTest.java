package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ViveiroTest {

    @Test
    @DisplayName("Deve criar viveiro com builder")
    void deveCriarViveiroComBuilder() {
        Fazenda fazenda = Fazenda.builder().id(1L).build();

        Viveiro viveiro = Viveiro.builder()
                .fazenda(fazenda)
                .codigo("V01")
                .nome("Viveiro Principal")
                .area(new BigDecimal("2.50"))
                .profundidadeMedia(new BigDecimal("1.20"))
                .volume(new BigDecimal("30000.00"))
                .status(StatusViveiroEnum.DISPONIVEL)
                .observacoes("Viveiro em excelente estado")
                .ativo(true)
                .build();

        assertThat(viveiro).isNotNull();
        assertThat(viveiro.getFazenda()).isEqualTo(fazenda);
        assertThat(viveiro.getCodigo()).isEqualTo("V01");
        assertThat(viveiro.getNome()).isEqualTo("Viveiro Principal");
        assertThat(viveiro.getArea()).isEqualByComparingTo("2.50");
        assertThat(viveiro.getStatus()).isEqualTo(StatusViveiroEnum.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve criar viveiro com construtor padrão")
    void deveCriarViveiroComConstrutorPadrao() {
        Viveiro viveiro = new Viveiro();
        viveiro.setCodigo("V02");
        viveiro.setNome("Viveiro Secundário");
        viveiro.setArea(new BigDecimal("1.80"));
        viveiro.setStatus(StatusViveiroEnum.OCUPADO);

        assertThat(viveiro.getCodigo()).isEqualTo("V02");
        assertThat(viveiro.getNome()).isEqualTo("Viveiro Secundário");
        assertThat(viveiro.getArea()).isEqualByComparingTo("1.80");
        assertThat(viveiro.getStatus()).isEqualTo(StatusViveiroEnum.OCUPADO);
    }

    @Test
    @DisplayName("Deve inicializar status como DISPONIVEL por padrão")
    void deveInicializarStatusComoDisponivelPorPadrao() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V03")
                .nome("Teste")
                .build();

        assertThat(viveiro.getStatus()).isEqualTo(StatusViveiroEnum.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve inicializar ativo como true por padrão")
    void deveInicializarAtivoComoTruePorPadrao() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V04")
                .nome("Teste")
                .build();

        assertThat(viveiro.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve inicializar lista de lotes vazia")
    void deveInicializarListaDeLotesVazia() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V05")
                .nome("Teste")
                .build();

        assertThat(viveiro.getLotes()).isNotNull();
        assertThat(viveiro.getLotes()).isEmpty();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V06")
                .nome("Teste")
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        viveiro.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(viveiro.getDataCriacao()).isNotNull();
        assertThat(viveiro.getDataAtualizacao()).isNotNull();
        assertThat(viveiro.getDataCriacao()).isBetween(antes, depois);
        assertThat(viveiro.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V07")
                .nome("Teste")
                .build();

        viveiro.onCreate();
        LocalDateTime dataOriginal = viveiro.getDataAtualizacao();

        Thread.sleep(10);

        viveiro.onUpdate();

        assertThat(viveiro.getDataAtualizacao()).isNotNull();
        assertThat(viveiro.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve adicionar lote ao viveiro")
    void deveAdicionarLoteAoViveiro() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V08")
                .nome("Teste")
                .build();

        Lote lote = Lote.builder().id(1L).build();

        viveiro.addLote(lote);

        assertThat(viveiro.getLotes()).hasSize(1);
        assertThat(viveiro.getLotes()).contains(lote);
        assertThat(lote.getViveiro()).isEqualTo(viveiro);
    }

    @Test
    @DisplayName("Deve remover lote do viveiro")
    void deveRemoverLoteDoViveiro() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V09")
                .nome("Teste")
                .build();

        Lote lote = Lote.builder().id(1L).build();

        viveiro.addLote(lote);
        assertThat(viveiro.getLotes()).hasSize(1);

        viveiro.removeLote(lote);

        assertThat(viveiro.getLotes()).isEmpty();
        assertThat(lote.getViveiro()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar diferentes status")
    void deveAceitarDiferentesStatus() {
        Viveiro viveiroDisponivel = Viveiro.builder()
                .codigo("V10")
                .nome("Disponível")
                .status(StatusViveiroEnum.DISPONIVEL)
                .build();

        Viveiro viveiroOcupado = Viveiro.builder()
                .codigo("V11")
                .nome("Ocupado")
                .status(StatusViveiroEnum.OCUPADO)
                .build();

        Viveiro viveiroManutencao = Viveiro.builder()
                .codigo("V12")
                .nome("Em Manutenção")
                .status(StatusViveiroEnum.MANUTENCAO)
                .build();

        assertThat(viveiroDisponivel.getStatus()).isEqualTo(StatusViveiroEnum.DISPONIVEL);
        assertThat(viveiroOcupado.getStatus()).isEqualTo(StatusViveiroEnum.OCUPADO);
        assertThat(viveiroManutencao.getStatus()).isEqualTo(StatusViveiroEnum.MANUTENCAO);
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisComoNull() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V13")
                .nome("Mínimo")
                .build();

        assertThat(viveiro.getArea()).isNull();
        assertThat(viveiro.getProfundidadeMedia()).isNull();
        assertThat(viveiro.getVolume()).isNull();
        assertThat(viveiro.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve permitir desativar viveiro")
    void devePermitirDesativarViveiro() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V14")
                .nome("Teste")
                .ativo(true)
                .build();

        assertThat(viveiro.getAtivo()).isTrue();

        viveiro.setAtivo(false);

        assertThat(viveiro.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve associar viveiro a uma fazenda")
    void deveAssociarViveiroAUmaFazenda() {
        Fazenda fazenda = Fazenda.builder().id(10L).build();

        Viveiro viveiro = Viveiro.builder()
                .fazenda(fazenda)
                .codigo("V15")
                .nome("Teste")
                .build();

        assertThat(viveiro.getFazenda()).isNotNull();
        assertThat(viveiro.getFazenda().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve adicionar múltiplos lotes ao viveiro")
    void deveAdicionarMultiplosLotesAoViveiro() {
        Viveiro viveiro = Viveiro.builder()
                .codigo("V16")
                .nome("Teste")
                .build();

        Lote lote1 = Lote.builder().id(1L).build();
        Lote lote2 = Lote.builder().id(2L).build();
        Lote lote3 = Lote.builder().id(3L).build();

        viveiro.addLote(lote1);
        viveiro.addLote(lote2);
        viveiro.addLote(lote3);

        assertThat(viveiro.getLotes()).hasSize(3);
        assertThat(viveiro.getLotes()).containsExactly(lote1, lote2, lote3);
    }
}
