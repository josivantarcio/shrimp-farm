package com.jtarcio.shrimpfarm.infrastructure.persistence;

import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByAtivoTrue();

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
