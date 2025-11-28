package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Biometria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BiometriaRepository extends JpaRepository<Biometria, Long> {

    List<Biometria> findByLoteIdOrderByDataBiometriaAsc(Long loteId);

    @Query("SELECT b FROM Biometria b WHERE b.lote.id = :loteId ORDER BY b.dataBiometria DESC LIMIT 1")
    Optional<Biometria> findUltimaBiometriaByLoteId(Long loteId);
}
