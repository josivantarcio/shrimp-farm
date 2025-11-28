package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Viveiro;
import com.jtarcio.shrimpfarm.domain.enums.StatusViveiroEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViveiroRepository extends JpaRepository<Viveiro, Long> {

    List<Viveiro> findByFazendaId(Long fazendaId);

    List<Viveiro> findByStatus(StatusViveiroEnum status);

    List<Viveiro> findByFazendaIdAndAtivoTrue(Long fazendaId);
}
