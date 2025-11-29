package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    /**
     * Busca fornecedor por CNPJ
     */
    Optional<Fornecedor> findByCnpj(String cnpj);

    /**
     * Busca fornecedores ativos
     */
    List<Fornecedor> findByAtivoTrue();

    /**
     * Busca fornecedores por nome (contendo)
     */
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCnpj(String cnpj);
}
