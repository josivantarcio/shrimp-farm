package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Racao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RacaoRepository extends JpaRepository<Racao, Long> {

    List<Racao> findByLoteIdOrderByDataAplicacaoAsc(Long loteId);

    @Query("SELECT SUM(r.custoTotal) FROM Racao r WHERE r.lote.id = :loteId")
    BigDecimal calcularCustoTotalRacaoByLoteId(Long loteId);

    @Query("SELECT SUM(r.quantidade) FROM Racao r WHERE r.lote.id = :loteId")
    BigDecimal calcularQuantidadeTotalRacaoByLoteId(Long loteId);
}
