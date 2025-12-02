package com.jtarcio.shrimpfarm.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FornecedorTest {

    @Test
    @DisplayName("Deve criar fornecedor com builder")
    void deveCriarFornecedorComBuilder() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Ração Premium Ltda")
                .cnpj("12.345.678/0001-90")
                .telefone("85999999999")
                .email("contato@racaopremium.com")
                .endereco("Av. Industrial, 1000")
                .cidade("Fortaleza")
                .estado("CE")
                .cep("60000-000")
                .contato("Pedro Alves")
                .observacoes("Fornecedor principal de ração")
                .ativo(true)
                .build();

        assertThat(fornecedor).isNotNull();
        assertThat(fornecedor.getNome()).isEqualTo("Ração Premium Ltda");
        assertThat(fornecedor.getCnpj()).isEqualTo("12.345.678/0001-90");
        assertThat(fornecedor.getEmail()).isEqualTo("contato@racaopremium.com");
        assertThat(fornecedor.getCidade()).isEqualTo("Fortaleza");
        assertThat(fornecedor.getEstado()).isEqualTo("CE");
    }

    @Test
    @DisplayName("Deve criar fornecedor com construtor padrão")
    void deveCriarFornecedorComConstrutorPadrao() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fertilizantes Norte");
        fornecedor.setCnpj("98.765.432/0001-10");
        fornecedor.setTelefone("8530001234");
        fornecedor.setEmail("vendas@fertilizantesnorte.com");

        assertThat(fornecedor.getNome()).isEqualTo("Fertilizantes Norte");
        assertThat(fornecedor.getCnpj()).isEqualTo("98.765.432/0001-10");
    }

    @Test
    @DisplayName("Deve inicializar ativo como true por padrão")
    void deveInicializarAtivoComoTruePorPadrao() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Fornecedor Teste")
                .build();

        assertThat(fornecedor.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        fornecedor.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(fornecedor.getDataCriacao()).isNotNull();
        assertThat(fornecedor.getDataAtualizacao()).isNotNull();
        assertThat(fornecedor.getDataCriacao()).isBetween(antes, depois);
        assertThat(fornecedor.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .build();

        fornecedor.onCreate();
        LocalDateTime dataOriginal = fornecedor.getDataAtualizacao();

        Thread.sleep(10);

        fornecedor.onUpdate();

        assertThat(fornecedor.getDataAtualizacao()).isNotNull();
        assertThat(fornecedor.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve permitir desativar fornecedor")
    void devePermitirDesativarFornecedor() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .ativo(true)
                .build();

        assertThat(fornecedor.getAtivo()).isTrue();

        fornecedor.setAtivo(false);

        assertThat(fornecedor.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisComoNull() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Fornecedor Mínimo")
                .build();

        assertThat(fornecedor.getCnpj()).isNull();
        assertThat(fornecedor.getTelefone()).isNull();
        assertThat(fornecedor.getEmail()).isNull();
        assertThat(fornecedor.getEndereco()).isNull();
        assertThat(fornecedor.getCidade()).isNull();
        assertThat(fornecedor.getEstado()).isNull();
        assertThat(fornecedor.getCep()).isNull();
        assertThat(fornecedor.getContato()).isNull();
        assertThat(fornecedor.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar endereço completo")
    void deveArmazenarEnderecoCompleto() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .endereco("Av. Comercial, 500")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01000-000")
                .build();

        assertThat(fornecedor.getEndereco()).isEqualTo("Av. Comercial, 500");
        assertThat(fornecedor.getCidade()).isEqualTo("São Paulo");
        assertThat(fornecedor.getEstado()).isEqualTo("SP");
        assertThat(fornecedor.getCep()).isEqualTo("01000-000");
    }

    @Test
    @DisplayName("Deve aceitar contato principal")
    void deveAceitarContatoPrincipal() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Fornecedor ABC")
                .contato("Ana Oliveira")
                .telefone("85988887777")
                .email("ana@fornecedor.com")
                .build();

        assertThat(fornecedor.getContato()).isEqualTo("Ana Oliveira");
        assertThat(fornecedor.getTelefone()).isEqualTo("85988887777");
        assertThat(fornecedor.getEmail()).isEqualTo("ana@fornecedor.com");
    }

    @Test
    @DisplayName("Deve armazenar observações")
    void deveArmazenarObservacoes() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .observacoes("Fornecedor com melhor preço, prazo de entrega 7 dias")
                .build();

        assertThat(fornecedor.getObservacoes()).isEqualTo("Fornecedor com melhor preço, prazo de entrega 7 dias");
    }

    @Test
    @DisplayName("Deve armazenar CNPJ obrigatório")
    void deveArmazenarCNPJ() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Empresa Fornecedora")
                .cnpj("11.222.333/0001-44")
                .build();

        assertThat(fornecedor.getCnpj()).isEqualTo("11.222.333/0001-44");
    }

    @Test
    @DisplayName("Deve criar fornecedor de diferentes tipos de produto")
    void deveCriarFornecedorDeDiferentesTiposDeProduto() {
        Fornecedor fornecedorRacao = Fornecedor.builder()
                .nome("Rações do Sul")
                .observacoes("Especializado em ração para camarão")
                .build();

        Fornecedor fornecedorFertilizante = Fornecedor.builder()
                .nome("Fertilizantes Nordeste")
                .observacoes("Fornece calcário e ureia")
                .build();

        assertThat(fornecedorRacao.getObservacoes()).contains("ração");
        assertThat(fornecedorFertilizante.getNome()).contains("Fertilizantes"); // CORRIGIDO: verifica o nome, não a observação
    }


    @Test
    @DisplayName("Deve aceitar estado com 2 caracteres")
    void deveAceitarEstadoCom2Caracteres() {
        Fornecedor fornecedor = Fornecedor.builder()
                .nome("Teste")
                .estado("CE")
                .build();

        assertThat(fornecedor.getEstado()).hasSize(2);
        assertThat(fornecedor.getEstado()).isEqualTo("CE");
    }
}
