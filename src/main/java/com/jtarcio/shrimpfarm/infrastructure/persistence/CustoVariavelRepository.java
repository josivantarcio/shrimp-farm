package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.CustoVariavel;
import com.jtarcio.shrimpfarm.domain.enums.CategoriaGastoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustoVariavelRepository extends JpaRepository<CustoVariavel, Long> {

    List<CustoVariavel> findByLoteIdOrderByDataLancamentoAsc(Long loteId);

    List<CustoVariavel> findByCategoria(CategoriaGastoEnum categoria);

    @Query("SELECT SUM(c.valor) FROM CustoVariavel c WHERE c.lote.id = :loteId")
    BigDecimal calcularCustoTotalVariavelByLoteId(Long loteId);
}
