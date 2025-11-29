package com.jtarcio.shrimpfarm.infrastructure.security;

import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import com.jtarcio.shrimpfarm.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com email: " + email
                ));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        return usuario; // Usuario já implementa UserDetails
    }
}
