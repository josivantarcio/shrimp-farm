package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Despesca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DespescaRepository extends JpaRepository<Despesca, Long> {

    Optional<Despesca> findByLoteId(Long loteId);
}
