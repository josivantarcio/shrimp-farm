package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Lista todos os usuários ativos.
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Verifica se já existe usuário com o email informado.
     */
    boolean existsByEmail(String email);
}
