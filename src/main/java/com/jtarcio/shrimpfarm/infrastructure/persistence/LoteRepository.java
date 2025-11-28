package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Lote;
import com.jtarcio.shrimpfarm.domain.enums.StatusLoteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByCodigo(String codigo);

    List<Lote> findByViveiroId(Long viveiroId);

    List<Lote> findByStatus(StatusLoteEnum status);

    @Query("SELECT l FROM Lote l WHERE l.viveiro.fazenda.id = :fazendaId")
    List<Lote> findByFazendaId(Long fazendaId);

    @Query("SELECT l FROM Lote l WHERE l.status = 'ATIVO' ORDER BY l.dataPovoamento DESC")
    List<Lote> findLotesAtivos();
}
