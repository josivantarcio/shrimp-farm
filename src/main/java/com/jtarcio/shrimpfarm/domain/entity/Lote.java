package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viveiro_id", nullable = false)
    private Viveiro viveiro;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo; // Ex: LOTE01_2025

    @Column(name = "data_povoamento", nullable = false)
    private LocalDate dataPovoamento;

    @Column(name = "data_despesca")
    private LocalDate dataDespesca;

    @Column(name = "quantidade_pos_larvas", nullable = false)
    private Integer quantidadePosLarvas; // Quantidade inicial de PLs

    @Column(name = "custo_pos_larvas", precision = 12, scale = 2)
    private BigDecimal custoPosLarvas; // Custo total das PLs

    @Column(name = "densidade_inicial", precision = 8, scale = 2)
    private BigDecimal densidadeInicial; // PLs por m²

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusLoteEnum status = StatusLoteEnum.PLANEJADO;

    @Column(name = "dias_cultivo")
    private Integer diasCultivo; // Calculado automaticamente

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Biometria> biometrias = new ArrayList<>();

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Racao> racoes = new ArrayList<>();

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Nutriente> nutrientes = new ArrayList<>();

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Fertilizacao> fertilizacoes = new ArrayList<>();

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustoVariavel> custosVariaveis = new ArrayList<>();

    @OneToOne(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Despesca despesca;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        calcularDiasCultivo();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        calcularDiasCultivo();
    }

    // Método para calcular dias de cultivo
    private void calcularDiasCultivo() {
        if (dataPovoamento != null) {
            LocalDate dataFim = (dataDespesca != null) ? dataDespesca : LocalDate.now();
            this.diasCultivo = (int) java.time.temporal.ChronoUnit.DAYS.between(dataPovoamento, dataFim);
        }
    }

    // Métodos auxiliares
    public void addBiometria(Biometria biometria) {
        biometrias.add(biometria);
        biometria.setLote(this);
    }

    public void addRacao(Racao racao) {
        racoes.add(racao);
        racao.setLote(this);
    }

    public void addNutriente(Nutriente nutriente) {
        nutrientes.add(nutriente);
        nutriente.setLote(this);
    }
}
