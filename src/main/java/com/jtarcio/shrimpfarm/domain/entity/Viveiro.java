package com.jtarcio.shrimpfarm.domain.entity;

import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "viveiros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Viveiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    @Column(nullable = false, length = 50)
    private String codigo; // Ex: V01, V02, Viveiro 03

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(precision = 10, scale = 2)
    private BigDecimal area; // em hectares

    @Column(name = "profundidade_media", precision = 5, scale = 2)
    private BigDecimal profundidadeMedia; // em metros

    @Column(precision = 12, scale = 2)
    private BigDecimal volume; // em m³

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusViveiroEnum status = StatusViveiroEnum.DISPONIVEL;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @OneToMany(mappedBy = "viveiro", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lote> lotes = new ArrayList<>();

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

    // Métodos auxiliares
    public void addLote(Lote lote) {
        lotes.add(lote);
        lote.setViveiro(this);
    }

    public void removeLote(Lote lote) {
        lotes.remove(lote);
        lote.setViveiro(null);
    }
}
