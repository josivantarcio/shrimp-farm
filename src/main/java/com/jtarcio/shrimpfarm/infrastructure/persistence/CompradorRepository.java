package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    /**
     * Busca comprador por CNPJ
     */
    Optional<Comprador> findByCnpj(String cnpj);

    /**
     * Busca compradores ativos
     */
    List<Comprador> findByAtivoTrue();

    /**
     * Busca compradores por nome (contendo)
     */
    List<Comprador> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCnpj(String cnpj);

}
