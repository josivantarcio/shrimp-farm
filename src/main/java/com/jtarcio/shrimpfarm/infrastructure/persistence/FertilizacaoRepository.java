package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Fertilizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FertilizacaoRepository extends JpaRepository<Fertilizacao, Long> {

    List<Fertilizacao> findByLoteIdOrderByDataAplicacaoAsc(Long loteId);

    @Query("SELECT SUM(f.custoTotal) FROM Fertilizacao f WHERE f.lote.id = :loteId")
    BigDecimal calcularCustoTotalFertilizacaoByLoteId(Long loteId);
}
