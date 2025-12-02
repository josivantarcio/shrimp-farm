package com.jtarcio.shrimpfarm.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompradorTest {

    @Test
    @DisplayName("Deve criar comprador com builder")
    void deveCriarCompradorComBuilder() {
        Comprador comprador = Comprador.builder()
                .nome("João Silva")
                .cpf("123.456.789-00")
                .telefone("85999999999")
                .email("joao@email.com")
                .endereco("Rua das Flores, 123")
                .cidade("Fortaleza")
                .estado("CE")
                .cep("60000-000")
                .contato("João Silva")
                .observacoes("Cliente preferencial")
                .ativo(true)
                .build();

        assertThat(comprador).isNotNull();
        assertThat(comprador.getNome()).isEqualTo("João Silva");
        assertThat(comprador.getCpf()).isEqualTo("123.456.789-00");
        assertThat(comprador.getEmail()).isEqualTo("joao@email.com");
        assertThat(comprador.getCidade()).isEqualTo("Fortaleza");
        assertThat(comprador.getEstado()).isEqualTo("CE");
    }

    @Test
    @DisplayName("Deve criar comprador pessoa jurídica com CNPJ")
    void deveCriarCompradorPessoaJuridicaComCNPJ() {
        Comprador comprador = Comprador.builder()
                .nome("Empresa XYZ Ltda")
                .cnpj("12.345.678/0001-90")
                .telefone("8530001234")
                .email("contato@empresaxyz.com")
                .endereco("Av. Comercial, 500")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01000-000")
                .ativo(true)
                .build();

        assertThat(comprador.getNome()).isEqualTo("Empresa XYZ Ltda");
        assertThat(comprador.getCnpj()).isEqualTo("12.345.678/0001-90");
        assertThat(comprador.getCpf()).isNull();
    }

    @Test
    @DisplayName("Deve criar comprador com construtor padrão")
    void deveCriarCompradorComConstrutorPadrao() {
        Comprador comprador = new Comprador();
        comprador.setNome("Maria Santos");
        comprador.setCpf("987.654.321-00");
        comprador.setTelefone("85988887777");
        comprador.setEmail("maria@email.com");

        assertThat(comprador.getNome()).isEqualTo("Maria Santos");
        assertThat(comprador.getCpf()).isEqualTo("987.654.321-00");
    }

    @Test
    @DisplayName("Deve inicializar ativo como true por padrão")
    void deveInicializarAtivoComoTruePorPadrao() {
        Comprador comprador = Comprador.builder()
                .nome("Comprador Teste")
                .build();

        assertThat(comprador.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Comprador comprador = Comprador.builder()
                .nome("Teste")
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        comprador.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(comprador.getDataCriacao()).isNotNull();
        assertThat(comprador.getDataAtualizacao()).isNotNull();
        assertThat(comprador.getDataCriacao()).isBetween(antes, depois);
        assertThat(comprador.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Comprador comprador = Comprador.builder()
                .nome("Teste")
                .build();

        comprador.onCreate();
        LocalDateTime dataOriginal = comprador.getDataAtualizacao();

        Thread.sleep(10);

        comprador.onUpdate();

        assertThat(comprador.getDataAtualizacao()).isNotNull();
        assertThat(comprador.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve permitir desativar comprador")
    void devePermitirDesativarComprador() {
        Comprador comprador = Comprador.builder()
                .nome("Teste")
                .ativo(true)
                .build();

        assertThat(comprador.getAtivo()).isTrue();

        comprador.setAtivo(false);

        assertThat(comprador.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisComoNull() {
        Comprador comprador = Comprador.builder()
                .nome("Comprador Mínimo")
                .build();

        assertThat(comprador.getCpf()).isNull();
        assertThat(comprador.getCnpj()).isNull();
        assertThat(comprador.getTelefone()).isNull();
        assertThat(comprador.getEmail()).isNull();
        assertThat(comprador.getEndereco()).isNull();
        assertThat(comprador.getCidade()).isNull();
        assertThat(comprador.getEstado()).isNull();
        assertThat(comprador.getCep()).isNull();
        assertThat(comprador.getContato()).isNull();
        assertThat(comprador.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar endereço completo")
    void deveArmazenarEnderecoCompleto() {
        Comprador comprador = Comprador.builder()
                .nome("Teste")
                .endereco("Rua Principal, 100")
                .cidade("Fortaleza")
                .estado("CE")
                .cep("60000-000")
                .build();

        assertThat(comprador.getEndereco()).isEqualTo("Rua Principal, 100");
        assertThat(comprador.getCidade()).isEqualTo("Fortaleza");
        assertThat(comprador.getEstado()).isEqualTo("CE");
        assertThat(comprador.getCep()).isEqualTo("60000-000");
    }

    @Test
    @DisplayName("Deve aceitar contato principal")
    void deveAceitarContatoPrincipal() {
        Comprador comprador = Comprador.builder()
                .nome("Empresa ABC")
                .contato("Carlos Silva")
                .telefone("85988887777")
                .build();

        assertThat(comprador.getContato()).isEqualTo("Carlos Silva");
        assertThat(comprador.getTelefone()).isEqualTo("85988887777");
    }

    @Test
    @DisplayName("Deve armazenar observações")
    void deveArmazenarObservacoes() {
        Comprador comprador = Comprador.builder()
                .nome("Teste")
                .observacoes("Cliente VIP, prioridade nas vendas")
                .build();

        assertThat(comprador.getObservacoes()).isEqualTo("Cliente VIP, prioridade nas vendas");
    }

    @Test
    @DisplayName("Deve aceitar CPF ou CNPJ")
    void deveAceitarCPFouCNPJ() {
        Comprador compradorPF = Comprador.builder()
                .nome("Pessoa Física")
                .cpf("123.456.789-00")
                .build();

        Comprador compradorPJ = Comprador.builder()
                .nome("Pessoa Jurídica")
                .cnpj("12.345.678/0001-90")
                .build();

        assertThat(compradorPF.getCpf()).isNotNull();
        assertThat(compradorPF.getCnpj()).isNull();

        assertThat(compradorPJ.getCnpj()).isNotNull();
        assertThat(compradorPJ.getCpf()).isNull();
    }
}
