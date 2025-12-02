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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos os usuários (ativos e inativos)");
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.toResponseList(usuarios);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAtivos() {
        log.info("Listando usuários ativos");
        List<Usuario> usuarios = usuarioRepository.findByAtivoTrue();
        return usuarioMapper.toResponseList(usuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuário por id: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com id: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com email: " + email));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        log.info("Criando novo usuário com email: {}, username: {}",
                request.getEmail(), request.getUsername());

        usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BusinessException("Já existe um usuário com o email: " + request.getEmail());
        });

        Usuario usuario = usuarioMapper.toEntity(request);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        log.info("Usuário criado com id: {}", usuarioSalvo.getId());
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        log.info("Atualizando usuário id: {}", id);

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new BusinessException(
                                "Já existe outro usuário com o email: " + request.getEmail());
                    }
                });

        usuarioMapper.updateEntity(usuarioExistente, request);
        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);

        log.info("Usuário atualizado id: {}", usuarioAtualizado.getId());
        return usuarioMapper.toResponse(usuarioAtualizado);
    }

    @Transactional
    public void desativar(Long id) {
        log.info("Desativando usuário id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado id: {}", id);
    }

    @Transactional
    public void inativar(Long id) {
        log.info("Inativando usuário id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário inativado id: {}", id);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando usuário id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuarioRepository.delete(usuario);

        log.info("Usuário deletado id: {}", id);
    }
}
