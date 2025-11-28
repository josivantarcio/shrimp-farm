package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Nutriente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NutrienteRepository extends JpaRepository<Nutriente, Long> {

    List<Nutriente> findByLoteIdOrderByDataAplicacaoAsc(Long loteId);

    @Query("SELECT SUM(n.custoTotal) FROM Nutriente n WHERE n.lote.id = :loteId")
    BigDecimal calcularCustoTotalNutrientesByLoteId(Long loteId);
}
