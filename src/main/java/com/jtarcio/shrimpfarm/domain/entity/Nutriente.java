package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.TipoNutrienteEnum;
import com.jtarcio.shrimpfarm.domain.enums.UnidadeMedidaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nutriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_nutriente", nullable = false, length = 30)
    private TipoNutrienteEnum tipoNutriente;

    @Column(nullable = false, length = 100)
    private String produto;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeMedidaEnum unidade;

    @Column(name = "custo_unitario", precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "custo_total", precision = 12, scale = 2)
    private BigDecimal custoTotal;

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
        calcularCustoTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        calcularCustoTotal();
    }

    private void calcularCustoTotal() {
        if (quantidade != null && custoUnitario != null) {
            this.custoTotal = quantidade.multiply(custoUnitario);
        }
    }
}
