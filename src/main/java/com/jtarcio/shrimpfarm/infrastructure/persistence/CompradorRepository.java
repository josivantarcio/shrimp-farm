package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    List<Comprador> findByAtivoTrue();
}
