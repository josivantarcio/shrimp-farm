package com.jtarcio.shrimpfarm.domain.entity;

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
@Table(name = "fazendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fazenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 100)
    private String proprietario;

    @Column(length = 200)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 10)
    private String cep;

    @Column(name = "area_total", precision = 10, scale = 2)
    private BigDecimal areaTotal; // em hectares

    @Column(name = "area_util", precision = 10, scale = 2)
    private BigDecimal areaUtil; // área de viveiros em hectares

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @OneToMany(mappedBy = "fazenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Viveiro> viveiros = new ArrayList<>();

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

    // Métodos auxiliares para gerenciar o relacionamento bidirecional
    public void addViveiro(Viveiro viveiro) {
        viveiros.add(viveiro);
        viveiro.setFazenda(this);
    }

    public void removeViveiro(Viveiro viveiro) {
        viveiros.remove(viveiro);
        viveiro.setFazenda(null);
    }
}
