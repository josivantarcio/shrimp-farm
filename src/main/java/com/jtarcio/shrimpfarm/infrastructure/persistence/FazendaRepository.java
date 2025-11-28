package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Fazenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    List<Fazenda> findByAtivaTrue();
}
