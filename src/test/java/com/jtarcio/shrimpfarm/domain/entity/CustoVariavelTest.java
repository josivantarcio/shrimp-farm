package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustoVariavelTest {

    @Test
    @DisplayName("Deve criar custo variável com builder")
    void deveCriarCustoVariavelComBuilder() {
        Lote lote = Lote.builder().id(1L).build();

        CustoVariavel custo = CustoVariavel.builder()
                .lote(lote)
                .dataLancamento(LocalDate.of(2024, 12, 1))
                .categoria(CategoriaGastoEnum.MAO_OBRA  )
                .descricao("Pagamento de funcionários")
                .valor(new BigDecimal("5000.00"))
                .observacoes("Pagamento quinzenal")
                .build();

        assertThat(custo).isNotNull();
        assertThat(custo.getLote()).isEqualTo(lote);
        assertThat(custo.getCategoria()).isEqualTo(CategoriaGastoEnum.MAO_OBRA);
        assertThat(custo.getDescricao()).isEqualTo("Pagamento de funcionários");
        assertThat(custo.getValor()).isEqualByComparingTo("5000.00");
    }

    @Test
    @DisplayName("Deve criar custo variável com construtor padrão")
    void deveCriarCustoVariavelComConstrutorPadrao() {
        CustoVariavel custo = new CustoVariavel();
        custo.setDataLancamento(LocalDate.now());
        custo.setCategoria(CategoriaGastoEnum.ENERGIA);
        custo.setDescricao("Conta de luz");
        custo.setValor(new BigDecimal("1200.00"));

        assertThat(custo.getCategoria()).isEqualTo(CategoriaGastoEnum.ENERGIA);
        assertThat(custo.getDescricao()).isEqualTo("Conta de luz");
        assertThat(custo.getValor()).isEqualByComparingTo("1200.00");
    }

    @Test
    @DisplayName("Deve definir dataCriacao e dataAtualizacao ao chamar onCreate")
    void deveDefinirDatasAoChamarOnCreate() {
        CustoVariavel custo = CustoVariavel.builder()
                .dataLancamento(LocalDate.now())
                .categoria(CategoriaGastoEnum.MANUTENCAO)
                .descricao("Reparo de aerador")
                .valor(new BigDecimal("800.00"))
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        custo.onCreate();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(custo.getDataCriacao()).isNotNull();
        assertThat(custo.getDataAtualizacao()).isNotNull();
        assertThat(custo.getDataCriacao()).isBetween(antes, depois);
        assertThat(custo.getDataAtualizacao()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve atualizar dataAtualizacao ao chamar onUpdate")
    void deveAtualizarDataAtualizacaoAoChamarOnUpdate() throws InterruptedException {
        CustoVariavel custo = CustoVariavel.builder()
                .dataLancamento(LocalDate.now())
                .categoria(CategoriaGastoEnum.COMBUSTIVEL)
                .descricao("Diesel")
                .valor(new BigDecimal("300.00"))
                .build();

        custo.onCreate();
        LocalDateTime dataOriginal = custo.getDataAtualizacao();

        Thread.sleep(10);

        custo.onUpdate();

        assertThat(custo.getDataAtualizacao()).isNotNull();
        assertThat(custo.getDataAtualizacao()).isAfterOrEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve aceitar diferentes categorias de gasto")
    void deveAceitarDiferentesCategoriasDeGasto() {
        CustoVariavel custoMaoObra = CustoVariavel.builder()
                .categoria(CategoriaGastoEnum.MAO_OBRA)
                .descricao("Funcionários")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        CustoVariavel custoEnergia = CustoVariavel.builder()
                .categoria(CategoriaGastoEnum.ENERGIA)
                .descricao("Eletricidade")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        CustoVariavel custoManutencao = CustoVariavel.builder()
                .categoria(CategoriaGastoEnum.MANUTENCAO)
                .descricao("Reparos")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        assertThat(custoMaoObra.getCategoria()).isEqualTo(CategoriaGastoEnum.MAO_OBRA);
        assertThat(custoEnergia.getCategoria()).isEqualTo(CategoriaGastoEnum.ENERGIA);
        assertThat(custoManutencao.getCategoria()).isEqualTo(CategoriaGastoEnum.MANUTENCAO);
    }

    @Test
    @DisplayName("Deve armazenar observações opcionais")
    void deveArmazenarObservacoesOpcionais() {
        CustoVariavel custoComObs = CustoVariavel.builder()
                .observacoes("Pagamento realizado em dia")
                .categoria(CategoriaGastoEnum.ENERGIA)
                .descricao("Teste")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        CustoVariavel custoSemObs = CustoVariavel.builder()
                .categoria(CategoriaGastoEnum.COMBUSTIVEL)
                .descricao("Teste")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        assertThat(custoComObs.getObservacoes()).isEqualTo("Pagamento realizado em dia");
        assertThat(custoSemObs.getObservacoes()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar valores decimais precisos")
    void deveAceitarValoresDecimaisPrecisos() {
        CustoVariavel custo = CustoVariavel.builder()
                .valor(new BigDecimal("1234.56"))
                .categoria(CategoriaGastoEnum.OUTROS)
                .descricao("Teste")
                .dataLancamento(LocalDate.now())
                .build();

        assertThat(custo.getValor()).isEqualByComparingTo("1234.56");
    }

    @Test
    @DisplayName("Deve aceitar custo categoria OUTROS")
    void deveAceitarCategoriaOutros() {
        CustoVariavel custo = CustoVariavel.builder()
                .categoria(CategoriaGastoEnum.OUTROS)
                .descricao("Despesa não categorizada")
                .valor(new BigDecimal("500.00"))
                .dataLancamento(LocalDate.now())
                .build();

        assertThat(custo.getCategoria()).isEqualTo(CategoriaGastoEnum.OUTROS);
    }

    @Test
    @DisplayName("Deve associar custo a um lote")
    void deveAssociarCustoAUmLote() {
        Lote lote = Lote.builder().id(10L).build();

        CustoVariavel custo = CustoVariavel.builder()
                .lote(lote)
                .categoria(CategoriaGastoEnum.MANUTENCAO)
                .descricao("Teste")
                .valor(BigDecimal.TEN)
                .dataLancamento(LocalDate.now())
                .build();

        assertThat(custo.getLote()).isNotNull();
        assertThat(custo.getLote().getId()).isEqualTo(10L);
    }
}
