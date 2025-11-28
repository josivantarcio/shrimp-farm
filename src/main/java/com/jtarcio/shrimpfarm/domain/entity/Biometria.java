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
@Table(name = "biometrias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Biometria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(name = "data_biometria", nullable = false)
    private LocalDate dataBiometria;

    @Column(name = "dia_cultivo", nullable = false)
    private Integer diaCultivo; // Dias desde o povoamento

    @Column(name = "peso_medio", nullable = false, precision = 8, scale = 3)
    private BigDecimal pesoMedio; // em gramas

    @Column(name = "quantidade_amostrada", nullable = false)
    private Integer quantidadeAmostrada; // Quantidade de camarões na amostra

    @Column(name = "peso_total_amostra", precision = 10, scale = 3)
    private BigDecimal pesoTotalAmostra; // em gramas

    // Campos calculados (serão preenchidos pelo Service)
    @Column(name = "ganho_peso_diario", precision = 8, scale = 4)
    private BigDecimal ganhoPesoDiario; // GPD em g/dia

    @Column(name = "biomassa_estimada", precision = 12, scale = 2)
    private BigDecimal biomassaEstimada; // em kg

    @Column(name = "sobrevivencia_estimada", precision = 5, scale = 2)
    private BigDecimal sobrevivenciaEstimada; // em %

    @Column(name = "fator_conversao_alimentar", precision = 5, scale = 3)
    private BigDecimal fatorConversaoAlimentar; // FCA

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
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
