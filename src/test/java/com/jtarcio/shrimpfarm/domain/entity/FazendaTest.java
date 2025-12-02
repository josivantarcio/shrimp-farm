package com.jtarcio.shrimpfarm.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FazendaTest {

    @Test
    @DisplayName("Deve criar fazenda com builder")
    void deveCriarFazendaComBuilder() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Aquamaris")
                .proprietario("João Silva")
                .endereco("Estrada do Camarão, km 5")
                .cidade("Aracati")
                .estado("CE")
                .cep("62800-000")
                .areaTotal(new BigDecimal("10.50"))
                .areaUtil(new BigDecimal("8.20"))
                .telefone("85999999999")
                .email("contato@aquamaris.com")
                .observacoes("Fazenda modelo em produção sustentável")
                .ativa(true)
                .build();

        assertThat(fazenda).isNotNull();
        assertThat(fazenda.getNome()).isEqualTo("Fazenda Aquamaris");
        assertThat(fazenda.getProprietario()).isEqualTo("João Silva");
        assertThat(fazenda.getCidade()).isEqualTo("Aracati");
        assertThat(fazenda.getEstado()).isEqualTo("CE");
        assertThat(fazenda.getAreaTotal()).isEqualByComparingTo("10.50");
        assertThat(fazenda.getAreaUtil()).isEqualByComparingTo("8.20");
    }

    @Test
    @DisplayName("Deve criar fazenda com construtor padrão")
    void deveCriarFazendaComConstrutorPadrao() {
        Fazenda fazenda = new Fazenda();
        fazenda.setNome("Fazenda Litoral");
        fazenda.setProprietario("Maria Santos");
        fazenda.setCidade("Fortaleza");
        fazenda.setEstado("CE");

        assertThat(fazenda.getNome()).isEqualTo("Fazenda Litoral");
        assertThat(fazenda.getProprietario()).isEqualTo("Maria Santos");
        assertThat(fazenda.getCidade()).isEqualTo("Fortaleza");
    }

    @Test
    @DisplayName("Deve inicializar ativa como true por padrão")
    void deveInicializarAtivaComoTruePorPadrao() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        assertThat(fazenda.getAtiva()).isTrue();
    }

    @Test
    @DisplayName("Deve inicializar lista de viveiros vazia")
    void deveInicializarListaDeViveirosVazia() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        assertThat(fazenda.getViveiros()).isNotNull();
        assertThat(fazenda.getViveiros()).isEmpty();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        fazenda.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(fazenda.getDataCriacao()).isNotNull();
        assertThat(fazenda.getDataAtualizacao()).isNotNull();
        assertThat(fazenda.getDataCriacao()).isBetween(antes, depois);
        assertThat(fazenda.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        fazenda.onCreate();
        LocalDateTime dataOriginal = fazenda.getDataAtualizacao();

        Thread.sleep(10);

        fazenda.onUpdate();

        assertThat(fazenda.getDataAtualizacao()).isNotNull();
        assertThat(fazenda.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve adicionar viveiro à fazenda")
    void deveAdicionarViveiroAFazenda() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        Viveiro viveiro = Viveiro.builder()
                .id(1L)
                .codigo("V01")
                .nome("Viveiro 01")
                .build();

        fazenda.addViveiro(viveiro);

        assertThat(fazenda.getViveiros()).hasSize(1);
        assertThat(fazenda.getViveiros()).contains(viveiro);
        assertThat(viveiro.getFazenda()).isEqualTo(fazenda);
    }

    @Test
    @DisplayName("Deve remover viveiro da fazenda")
    void deveRemoverViveiroDaFazenda() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        Viveiro viveiro = Viveiro.builder()
                .id(1L)
                .codigo("V01")
                .nome("Viveiro 01")
                .build();

        fazenda.addViveiro(viveiro);
        assertThat(fazenda.getViveiros()).hasSize(1);

        fazenda.removeViveiro(viveiro);

        assertThat(fazenda.getViveiros()).isEmpty();
        assertThat(viveiro.getFazenda()).isNull();
    }

    @Test
    @DisplayName("Deve adicionar múltiplos viveiros à fazenda")
    void deveAdicionarMultiplosViveirosAFazenda() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .build();

        Viveiro viveiro1 = Viveiro.builder().id(1L).codigo("V01").nome("Viveiro 01").build();
        Viveiro viveiro2 = Viveiro.builder().id(2L).codigo("V02").nome("Viveiro 02").build();
        Viveiro viveiro3 = Viveiro.builder().id(3L).codigo("V03").nome("Viveiro 03").build();

        fazenda.addViveiro(viveiro1);
        fazenda.addViveiro(viveiro2);
        fazenda.addViveiro(viveiro3);

        assertThat(fazenda.getViveiros()).hasSize(3);
        assertThat(fazenda.getViveiros()).containsExactly(viveiro1, viveiro2, viveiro3);
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisComoNull() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Mínima")
                .build();

        assertThat(fazenda.getProprietario()).isNull();
        assertThat(fazenda.getEndereco()).isNull();
        assertThat(fazenda.getCidade()).isNull();
        assertThat(fazenda.getEstado()).isNull();
        assertThat(fazenda.getCep()).isNull();
        assertThat(fazenda.getAreaTotal()).isNull();
        assertThat(fazenda.getAreaUtil()).isNull();
        assertThat(fazenda.getTelefone()).isNull();
        assertThat(fazenda.getEmail()).isNull();
        assertThat(fazenda.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve permitir desativar fazenda")
    void devePermitirDesativarFazenda() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Teste")
                .ativa(true)
                .build();

        assertThat(fazenda.getAtiva()).isTrue();

        fazenda.setAtiva(false);

        assertThat(fazenda.getAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve armazenar endereço completo")
    void deveArmazenarEnderecoCompleto() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Costa")
                .endereco("Rodovia CE-040, km 30")
                .cidade("Aquiraz")
                .estado("CE")
                .cep("61700-000")
                .build();

        assertThat(fazenda.getEndereco()).isEqualTo("Rodovia CE-040, km 30");
        assertThat(fazenda.getCidade()).isEqualTo("Aquiraz");
        assertThat(fazenda.getEstado()).isEqualTo("CE");
        assertThat(fazenda.getCep()).isEqualTo("61700-000");
    }

    @Test
    @DisplayName("Deve armazenar informações de contato")
    void deveArmazenarInformacoesDeContato() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Maré")
                .telefone("85988887777")
                .email("contato@fazendamare.com.br")
                .build();

        assertThat(fazenda.getTelefone()).isEqualTo("85988887777");
        assertThat(fazenda.getEmail()).isEqualTo("contato@fazendamare.com.br");
    }

    @Test
    @DisplayName("Deve armazenar observações")
    void deveArmazenarObservacoes() {
        Fazenda fazenda = Fazenda.builder()
                .nome("Fazenda Sol")
                .observacoes("Certificada em produção orgânica")
                .build();

        assertThat(fazenda.getObservacoes()).isEqualTo("Certificada em produção orgânica");
    }
}
