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
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // login pode ser email OU username
        Usuario usuario = usuarioRepository.findByEmail(login)
                .or(() -> usuarioRepository.findByUsername(login))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com email/username: " + login
                ));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new UsernameNotFoundException("Usuário inativo: " + login);
        }

        // Usuario já implementa UserDetails, então podemos devolver direto
        return usuario;
    }
}
