package com.jtarcio.shrimpfarm.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "despescas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despesca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false, unique = true)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;

    @Column(name = "data_despesca", nullable = false)
    private LocalDate dataDespesca;

    @Column(name = "peso_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal pesoTotal; // em kg

    @Column(name = "quantidade_despescada", nullable = false)
    private Integer quantidadeDespescada; // Número de camarões

    @Column(name = "peso_medio_final", nullable = false, precision = 8, scale = 3)
    private BigDecimal pesoMedioFinal; // em gramas

    @Column(name = "taxa_sobrevivencia", precision = 5, scale = 2)
    private BigDecimal taxaSobrevivencia; // em %

    @Column(name = "preco_venda_kg", precision = 10, scale = 2)
    private BigDecimal precoVendaKg; // R$/kg

    @Column(name = "receita_total", precision = 12, scale = 2)
    private BigDecimal receitaTotal;

    @Column(name = "custo_despesca", precision = 10, scale = 2)
    private BigDecimal custoDespesca; // Custo operacional da despesca

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        calcularReceita();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        calcularReceita();
    }

    private void calcularReceita() {
        if (pesoTotal != null && precoVendaKg != null) {
            this.receitaTotal = pesoTotal.multiply(precoVendaKg);
        }
    }
}
