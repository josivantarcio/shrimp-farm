package com.jtarcio.shrimpfarm.application.service;

import com.jtarcio.shrimpfarm.application.dto.request.UsuarioRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.application.mapper.UsuarioMapper;
import com.jtarcio.shrimpfarm.domain.entity.Usuario;
import com.jtarcio.shrimpfarm.domain.exception.BusinessException;
import com.jtarcio.shrimpfarm.domain.exception.EntityNotFoundException;
import com.jtarcio.shrimpfarm.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        log.info("Criando novo usuário: {}", request.getEmail());

        // Validar email único
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um usuário com o email: " + request.getEmail());
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.debug("Buscando usuário por ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com email " + email + " não encontrado"));

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        log.debug("Listando todos os usuários");

        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAtivos() {
        log.debug("Listando usuários ativos");

        return usuarioRepository.findByAtivoTrue().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando usuários paginados: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponse);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        // Validar email único (exceto o próprio)
        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new BusinessException("Já existe outro usuário com o email: " + request.getEmail());
                    }
                });

        usuarioMapper.updateEntity(usuario, request);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        log.info("Usuário atualizado com sucesso. ID: {}", id);
        return usuarioMapper.toResponse(usuarioAtualizado);
    }

    @Transactional
    public void alterarSenha(Long id, String novaSenha) {
        log.info("Alterando senha do usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        log.info("Senha alterada com sucesso. Usuário ID: {}", id);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        usuarioRepository.delete(usuario);
        log.info("Usuário deletado com sucesso. ID: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário inativado com sucesso. ID: {}", id);
    }
}
